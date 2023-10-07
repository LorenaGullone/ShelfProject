package com.shelf.shelfproject.repositories;


import com.shelf.shelfproject.entities.Ordine;
import com.shelf.shelfproject.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrdineRepository extends JpaRepository<Ordine, Long> {

    List<Ordine> findByAcquirente(Utente utente);
    List<Ordine> findByDettagliAcquisto(Date date);

    @Query("select o from Ordine o where o.data > ?1 and o.data < ?2 and o.acquirente = ?3")
    List<Ordine> findByBuyerInPeriod(Date startDate, Date endDate, Utente utente);
}
