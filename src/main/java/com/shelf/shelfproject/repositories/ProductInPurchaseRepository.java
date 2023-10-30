package com.shelf.shelfproject.repositories;


import com.shelf.shelfproject.entities.Cart;
import com.shelf.shelfproject.entities.Product;
import com.shelf.shelfproject.entities.ProductInPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInPurchaseRepository extends JpaRepository<ProductInPurchase, Long> {

    boolean existsByCartAndProduct(Cart cart, Product product);

}//DettaglioCarrelloRepository
