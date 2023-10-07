package com.shelf.shelfproject.repositories;

import com.shelf.shelfproject.entities.Prodotto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProdottoRepository extends JpaRepository<Prodotto, Long> {
    List<Prodotto> findByNomeContaining(String nome);
    List<Prodotto> findByBarCode(String barCode);
    List<Prodotto> findByGenere(String genere);
    boolean existsByBarCode(String barCode);

    @Query("SELECT p " +
            "FROM Prodotto p " +
            "WHERE (p.nome = ?1 OR p.nome IS NULL) AND " +
            "(p.prezzo = ?2 OR p.prezzo IS NULL) AND " +
            "(p.genere = ?3 OR p.genere IS NULL)")
    List<Prodotto> advancedSearch(String nome, Float prezzo, String genere);


}
