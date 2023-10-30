package com.shelf.shelfproject.repositories;

import com.shelf.shelfproject.entities.Cart;
import com.shelf.shelfproject.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByUser(User user);
}
