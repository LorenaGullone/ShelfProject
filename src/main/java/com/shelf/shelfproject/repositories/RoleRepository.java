package com.shelf.shelfproject.repositories;

import com.shelf.shelfproject.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByType(String type);
}
