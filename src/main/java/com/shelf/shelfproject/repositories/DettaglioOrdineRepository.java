package com.shelf.shelfproject.repositories;


import com.shelf.shelfproject.entities.DettaglioOrdine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DettaglioOrdineRepository extends JpaRepository<DettaglioOrdine, Long> {

}//DettaglioCarrelloRepository
