package com.shelf.shelfproject.repositories;

import com.shelf.shelfproject.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {

    List<Utente> findByNome(String nome);
    List<Utente> findByCognome(String cognome);
    List<Utente> findByNomeAndCognome(String nome, String cognome);
    List<Utente> findByEmail(String email);
    boolean existsByEmail(String email);

}//UtenteRepository
