package com.shelf.shelfproject.services;

import com.shelf.shelfproject.entities.*;
import com.shelf.shelfproject.repositories.ProductInPurchaseRepository;
import com.shelf.shelfproject.repositories.ProductRepository;
import com.shelf.shelfproject.repositories.PurchaseRepository;
import com.shelf.shelfproject.repositories.UserRepository;
import com.shelf.shelfproject.security.JwtUtils;
import com.shelf.shelfproject.support.exceptions.*;
import com.shelf.shelfproject.support.purchasing.PurchaseDetailsDTO;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PurchasingService {
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private ProductInPurchaseRepository productInPurchaseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    JwtUtils jwtUtils;


    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW,
            rollbackFor = {UserNotFoundException.class, PriceChangedException.class, InconsistentCartException.class, ProductNotFoundException.class, OptimisticLockException.class})
    public Purchase buy (@Valid List<PurchaseDetailsDTO> pipDetailsList) throws UserNotFoundException, PriceChangedException, InconsistentCartException, ProductNotFoundException, NotAvailableQuantityException {
        Optional<User> user = userRepository.findByUsername(jwtUtils.getUsername());
        if( user.isEmpty() )
            throw new UserNotFoundException();
        Cart cart = user.get().getCart();

        if( cart.getProducts().size() != pipDetailsList.size())
            //accesso concorrente a uno stesso carrello
            throw new InconsistentCartException();


        Purchase purchase = new Purchase();
        purchase.setBuyer(user.get());
        purchase.setTimeStamp(new Date(System.currentTimeMillis()));
        purchase.setProducts(new LinkedList<>());
        purchaseRepository.save(purchase);

        for( PurchaseDetailsDTO detail : pipDetailsList ){
            Optional<ProductInPurchase> related_pip = productInPurchaseRepository.findById(detail.getId());
            if( related_pip.isEmpty() || related_pip.get().getProduct().getId() != detail.getProduct())
                throw new InconsistentCartException();

            Optional<Product> found = productRepository.findById(detail.getProduct());
            if( found.isEmpty() )
                throw new ProductNotFoundException();
            Product product = found.get();
            if( product.getQuantity() < detail.getQuantity() )
                throw new NotAvailableQuantityException(product.getBarCode());

            if( product.getPrice() != detail.getUnitPrice() )
                throw new PriceChangedException(product.getName());

            if( detail.getQuantity() > 0){
                ProductInPurchase pip = new ProductInPurchase();
                pip.setQuantity(detail.getQuantity());
                pip.setPrice(detail.getUnitPrice());
                pip.setPurchase(purchase);
                pip.setProduct(product);
                productInPurchaseRepository.save(pip);
                purchase.getProducts().add(pip);
                product.setQuantity(product.getQuantity() - pip.getQuantity());
                cart.getProducts().remove(pip);
                productInPurchaseRepository.delete(pip);
            }

        }

        return purchase;
    }

    @Transactional(readOnly = true, propagation = Propagation.NESTED, isolation = Isolation.READ_COMMITTED, rollbackFor = {UserNotFoundException.class})
    public List<Purchase> getPurchasesByUser (int pageNumber, int pageSize,String sortBy) throws UserNotFoundException {
        Optional<User> user = userRepository.findByUsername(jwtUtils.getUsername());
        if( user.isEmpty() )
            throw new UserNotFoundException();
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Purchase> pagedResult = purchaseRepository.findAllByBuyer(user.get(), paging);

        if ( pagedResult.hasContent() ) {
            return pagedResult.getContent();
        }
        else {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.NESTED, isolation = Isolation.READ_COMMITTED, rollbackFor = {UserNotFoundException.class, WrongTimelineException.class})
    public List<Purchase> getPurchasesByUserInPeriod(Date startDate, Date endDate) throws UserNotFoundException, WrongTimelineException {
        Optional<User> user = userRepository.findByUsername(jwtUtils.getUsername());
        if( user.isEmpty() )
            throw new UserNotFoundException();
        if ( startDate.compareTo(endDate) >= 0 ) {
            throw new WrongTimelineException();
        }
        return purchaseRepository.findByBuyerInPeriod(startDate, endDate, user.get());
    }

    @Transactional(readOnly = true, propagation = Propagation.NESTED, isolation = Isolation.READ_COMMITTED)
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

}
