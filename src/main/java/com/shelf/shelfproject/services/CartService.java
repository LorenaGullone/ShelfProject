package com.shelf.shelfproject.services;

import com.shelf.shelfproject.entities.Cart;
import com.shelf.shelfproject.entities.Product;
import com.shelf.shelfproject.entities.ProductInPurchase;
import com.shelf.shelfproject.entities.User;
import com.shelf.shelfproject.repositories.CartRepository;
import com.shelf.shelfproject.repositories.ProductInPurchaseRepository;
import com.shelf.shelfproject.repositories.ProductRepository;
import com.shelf.shelfproject.repositories.UserRepository;
import com.shelf.shelfproject.security.JwtUtils;
import com.shelf.shelfproject.support.exceptions.NotAvailableQuantityException;
import com.shelf.shelfproject.support.exceptions.ProductInPurchaseNotFoundException;
import com.shelf.shelfproject.support.exceptions.ProductNotFoundException;
import com.shelf.shelfproject.support.exceptions.UserNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CartService {
    @Autowired
    private ProductInPurchaseRepository productInPurchaseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    JwtUtils jwtUtils;

    @Transactional(readOnly = false, propagation = Propagation.NESTED,
            rollbackFor = {UserNotFoundException.class, OptimisticLockException.class}, isolation = Isolation.READ_COMMITTED)
    public List<ProductInPurchase> getCart() throws UserNotFoundException{
        Optional<User> user = userRepository.findByUsername(jwtUtils.getUsername());
        if( user.isEmpty() )
            throw new UserNotFoundException();
        Cart cart = cartRepository.findByUser(user.get());
        if(cart != null){
            for(ProductInPurchase productInPurchase : cart.getProducts()){
                Product product = productInPurchase.getProduct();
                if(productInPurchase.getQuantity() > product.getQuantity()){
                    //si fa questo per aggiornare correttamente la quantità quando si visualizza il carrello
                    //vedere come segnalare all'utente la modifica della quantità nel carrello
                    productInPurchase.setQuantity(product.getQuantity());
                    productInPurchase.setPrice(product.getPrice());
                }
            }
            return cart.getProducts();
        } else {
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.NESTED,
            rollbackFor = {UserNotFoundException.class, OptimisticLockException.class, NotAvailableQuantityException.class}, isolation = Isolation.SERIALIZABLE)
    public Cart addProduct(long idProd) throws UserNotFoundException, ProductNotFoundException, NotAvailableQuantityException {
        Optional<User> userFound = userRepository.findByUsername(jwtUtils.getUsername());
        if( userFound.isEmpty() )
            throw new UserNotFoundException();
        User user = userFound.get();
        Optional<Product> taken = productRepository.findById(idProd);
        if( taken.isEmpty() )
            throw new ProductNotFoundException();
        Product productToAdd = taken.get();
        if( productToAdd.getQuantity()<1 )
            throw new NotAvailableQuantityException();

        ProductInPurchase detail = new ProductInPurchase();
        detail.setProduct(productToAdd);
        Cart cart = cartRepository.findByUser(user);
        if(cart == null) {
            //creazione del primo carrello per l'utente
            cart = new Cart();
            cart.setProducts(new LinkedList<>());
            cart.setUser(user);
            user.setCart(cart);
            //aggiunta del nuovo prodotto
            detail.setQuantity(1);
            detail.setPrice(productToAdd.getPrice());
            cart.getProducts().add(detail);
        } else {
            //esiste un carrello associato allo specifico utente
            if( productInPurchaseRepository.existsByCartAndProduct(cart, productToAdd) ) {
                //il prodotto è già presente nel carrello per cui si incrementa la sua quantità
                for( ProductInPurchase pip : cart.getProducts() ){
                    if( pip.getProduct().getId() == idProd ){
                        pip.setQuantity(pip.getQuantity()+1);
                        pip.setPrice(productToAdd.getPrice());
                    }
                }
            }
            else {
                //il prodotto non è presente per cui si aggiunge il ProductInPurchase al cart
                detail.setQuantity(1);
                detail.setPrice(productToAdd.getPrice());
                detail.setCart(cart);
                productToAdd.getInPurchases().add(detail);
                cart.getProducts().add(detail);
                productInPurchaseRepository.save(detail);
            }
        }
        return cart;
    }

    @Transactional(readOnly = false, propagation = Propagation.NESTED,
            rollbackFor = {UserNotFoundException.class, OptimisticLockException.class, NotAvailableQuantityException.class}, isolation = Isolation.SERIALIZABLE)
    public Cart addProduct(long idProd, int quantity) throws UserNotFoundException, ProductNotFoundException, NotAvailableQuantityException {
        Optional<User> userFound = userRepository.findByUsername(jwtUtils.getUsername());
        if( userFound.isEmpty() )
            throw new UserNotFoundException();
        User user = userFound.get();
        Optional<Product> taken = productRepository.findById(idProd);
        if( taken.isEmpty() )
            throw new ProductNotFoundException();
        Product productToAdd = taken.get();
        if( quantity>productToAdd.getQuantity() )
            throw new NotAvailableQuantityException();

        ProductInPurchase detail = new ProductInPurchase();
        detail.setProduct(productToAdd);
        Cart cart = cartRepository.findByUser(user);
        if(cart == null) {
            //creazione del primo carrello per l'utente
            cart = new Cart();
            cart.setProducts(new LinkedList<>());
            cart.setUser(user);
            user.setCart(cart);
            //aggiunta del nuovo prodotto
            detail.setQuantity(quantity);
            detail.setPrice(productToAdd.getPrice());
            cart.getProducts().add(detail);
        } else {
            //esiste un carrello associato allo specifico utente
            if( productInPurchaseRepository.existsByCartAndProduct(cart, productToAdd) ) {
                //il prodotto è già presente nel carrello per cui si incrementa la sua quantità
                for( ProductInPurchase pip : cart.getProducts() ){
                    if( pip.getProduct().getId() == idProd ){
                        pip.setQuantity(pip.getQuantity()+quantity);
                        pip.setPrice(productToAdd.getPrice());
                    }
                }
            }
            else {
                //il prodotto non è presente per cui si aggiunge il ProductInPurchase al cart
                detail.setQuantity(1);
                detail.setPrice(productToAdd.getPrice());
                detail.setCart(cart);
                productToAdd.getInPurchases().add(detail);
                cart.getProducts().add(detail);
                productInPurchaseRepository.save(detail);
            }
        }
        return cart;
    }

    @Transactional(readOnly = false, propagation = Propagation.NESTED, rollbackFor = {UserNotFoundException.class}, isolation = Isolation.SERIALIZABLE)
    public void removeProduct(long idProdInPurchase) throws UserNotFoundException, ProductInPurchaseNotFoundException {
        Optional<User> userFound = userRepository.findByUsername(jwtUtils.getUsername());
        if( userFound.isEmpty() )
            throw new UserNotFoundException();
        User user = userFound.get();
        Optional<ProductInPurchase> pip = productInPurchaseRepository.findById(idProdInPurchase);
        if( pip.isEmpty() )
            throw new ProductInPurchaseNotFoundException();
        Cart cart = pip.get().getCart();
        cart.getProducts().remove(pip.get());
        //osservazione: il cartello è in stato managed
        productInPurchaseRepository.delete(pip.get());
    }

    @Transactional(readOnly = false, propagation = Propagation.NESTED, rollbackFor = {UserNotFoundException.class}, isolation = Isolation.SERIALIZABLE)
    public void removeAllProduct() throws UserNotFoundException {
        Optional<User> userFound = userRepository.findByUsername(jwtUtils.getUsername());
        if( userFound.isEmpty() )
            throw new UserNotFoundException();
        User user = userFound.get();
        Cart cart = cartRepository.findByUser(user);
        productInPurchaseRepository.deleteAll(cart.getProducts());
        cart.getProducts().clear();
    }

    @Transactional(readOnly = false, propagation = Propagation.NESTED, rollbackFor = {UserNotFoundException.class, OptimisticLockException.class}, isolation = Isolation.READ_COMMITTED)
    public void plusOne(long idProdInPurchase) throws UserNotFoundException, ProductInPurchaseNotFoundException {
        Optional<User> userFound = userRepository.findByUsername(jwtUtils.getUsername());
        if( userFound.isEmpty() )
            throw new UserNotFoundException();
        User user = userFound.get();
        Optional<ProductInPurchase> pip = productInPurchaseRepository.findById(idProdInPurchase);
        if(pip.isEmpty())
            throw new ProductInPurchaseNotFoundException();
        int oldQuantity = pip.get().getQuantity();
        pip.get().setQuantity(oldQuantity+1);
    }

    @Transactional(readOnly = false, propagation = Propagation.NESTED, rollbackFor = {UserNotFoundException.class, OptimisticLockException.class}, isolation = Isolation.READ_COMMITTED)
    public void minusOne(long idProdInPurchase) throws UserNotFoundException, ProductInPurchaseNotFoundException {
        Optional<User> userFound = userRepository.findByUsername(jwtUtils.getUsername());
        if( userFound.isEmpty() )
            throw new UserNotFoundException();
        User user = userFound.get();
        Optional<ProductInPurchase> pip = productInPurchaseRepository.findById(idProdInPurchase);
        if(pip.isEmpty())
            throw new ProductInPurchaseNotFoundException();
        int oldQuantity = pip.get().getQuantity();
        if( oldQuantity-1 == 0 ) {
            Cart cart = pip.get().getCart();
            cart.getProducts().remove(pip.get());
            productInPurchaseRepository.delete(pip.get());
        }
        pip.get().setQuantity(oldQuantity-1);

    }

}
