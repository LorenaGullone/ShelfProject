package com.shelf.shelfproject.repositories;

import com.shelf.shelfproject.entities.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContaining(String name);
    List<Product> findByNameIgnoreCase(String name);
    List<Product> findByBarCode(String barCode);
    List<Product> findByStyleAndCategory(String style, String category);
    List<Product> findByNameAndStyle(String name, String style);

    boolean existsByBarCode(String barCode);
    boolean existsProductByNameIgnoreCase(String productName);

    @Query("SELECT p " +
            "FROM Product p " +
            "WHERE (p.name = ?1 OR p.name IS NULL) AND " +
            "(p.price > ?2 OR p.price IS NULL) AND " +
            "(p.quantity > ?3 OR p.quantity IS NULL) AND " +
            "(p.category > ?4 OR p.category IS NULL) AND " +
            "(p.style = ?5 OR p.style IS NULL)")
    List<Product> advancedSearch(String name, Float price, Integer quantity, String category, String style);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT p FROM Product p WHERE p.id=?1")
    Product findByIdWithLock(Long id);

}//ProductRepository
