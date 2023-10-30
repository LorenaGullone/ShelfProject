package com.shelf.shelfproject.services;

import com.shelf.shelfproject.entities.User;
import com.shelf.shelfproject.repositories.UserRepository;
import com.shelf.shelfproject.support.exceptions.MailUserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountingService {

    @Autowired
    private UserRepository userRepository;

    //metodo per la registrazione di un nuovo utente
    //il metodo restituisce un oggetto User per verificare che la creazione dell'utente sia avvenuta correttamente
    //update: la registrazione viene effettuata tramite keycloak
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public User userSignUp(User user) throws MailUserAlreadyExistsException {
        if ( userRepository.existsByEmail(user.getEmail()) ) {
            throw new MailUserAlreadyExistsException();
        }
        return userRepository.save(user);
    }

    //metodo che restiruisce la lista degli utenti registrati
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}
