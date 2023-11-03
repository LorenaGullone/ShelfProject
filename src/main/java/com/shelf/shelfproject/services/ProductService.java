package com.shelf.shelfproject.services;

import com.shelf.shelfproject.entities.Product;
import com.shelf.shelfproject.repositories.ProductRepository;
import com.shelf.shelfproject.support.exceptions.BarCodeAlreadyExistException;
import com.shelf.shelfproject.support.exceptions.CategoryNotFoundException;
import com.shelf.shelfproject.support.exceptions.StyleNotFoundException;
import com.shelf.shelfproject.support.exceptions.ValidationFailed;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = false, rollbackFor = {BarCodeAlreadyExistException.class},
            isolation = Isolation.READ_COMMITTED)
    public Product addProduct(Product product) throws BarCodeAlreadyExistException {
        if( product.getBarCode() == null || product.getName() == null )
            throw new ValidationFailed();
        if ( productRepository.existsByBarCode(product.getBarCode())) {
            throw new BarCodeAlreadyExistException();
        }
        return productRepository.save(product);
    }

    @Transactional(readOnly = false, rollbackFor = {BarCodeAlreadyExistException.class},
            isolation = Isolation.READ_COMMITTED)
    public Product addProduct(String name, String barCode, String description, String image, String style, String category, Integer quantity, Float price) throws BarCodeAlreadyExistException, CategoryNotFoundException, StyleNotFoundException {
        if ( productRepository.existsByBarCode(barCode)) {
            throw new BarCodeAlreadyExistException();
        }
        Product product = new Product();
        product.setName(name);
        product.setBarCode(barCode);
        product.setDescription(description);
        product.setImage(image);
        product.setPrice(price);
        product.setQuantity(quantity);
        return productRepository.save(product);
    }

    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, rollbackFor = {ValidationFailed.class, OptimisticLockException.class})
    public Product updateQuantity(String barCode, int quantity) {
        //vedere se usare metodo con lock
        List<Product> result = productRepository.findByBarCode(barCode);
        if( result.isEmpty() || quantity <= 0 )
            throw new ValidationFailed();
        Product product = result.get(0);
        int oldQnt = product.getQuantity();
        product.setQuantity(oldQnt+quantity);
        return product;
    }

    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, rollbackFor = {ValidationFailed.class, OptimisticLockException.class})
    public Product updatePrice(long idProdotto, int price) {
        //vedere se usare metodo con lock
        Optional<Product> result = productRepository.findById(idProdotto);
        if( result.isEmpty() || price <= 0 )
            throw new ValidationFailed();
        Product product = result.get();
        product.setQuantity(price);
        return product;
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<Product> showAllProducts() {
        return productRepository.findAll();
    }


    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<Product> showAllProducts(int pageNumber, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Product> pagedResult = productRepository.findAll(paging);
        if ( pagedResult.hasContent() ) {
            return pagedResult.getContent();
        }
        else {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Product> showProductsByName(String name) {
        return productRepository.findByNameIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public List<Product> showProductsByBarCode(String barCode) {
        return productRepository.findByBarCode(barCode);
    }




}
