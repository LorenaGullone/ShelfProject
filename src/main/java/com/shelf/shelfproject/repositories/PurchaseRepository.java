package com.shelf.shelfproject.repositories;


import com.shelf.shelfproject.entities.Purchase;
import com.shelf.shelfproject.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findByBuyer(User user);
    List<Purchase> findByTimeStamp(Date date);
    //List<Ordine> findByDettagliOrdine(Date date);

    Page<Purchase> findAllByBuyer(User user, Pageable paging);
    @Query("select o from Purchase o where o.timeStamp > ?1 and o.timeStamp < ?2 and o.buyer = ?3")
    List<Purchase> findByBuyerInPeriod(Date startDate, Date endDate, User user);

}//OrdineRepository
