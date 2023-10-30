package com.shelf.shelfproject.services;

import com.shelf.shelfproject.entities.User;
import com.shelf.shelfproject.payload.request.RegisterRequest;
import com.shelf.shelfproject.repositories.RoleRepository;
import com.shelf.shelfproject.repositories.UserRepository;
import com.shelf.shelfproject.support.exceptions.MailUserAlreadyExistsException;
import com.shelf.shelfproject.support.exceptions.UsernameUserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    public User update(Long id, User user) {
        User toUpdate = userRepository.findById(id).orElse(null);
        assert toUpdate != null;

        toUpdate.setName(user.getName());
        toUpdate.setSurname(user.getSurname());
        toUpdate.setBirthdayDate(user.getBirthdayDate());
        toUpdate.setUsername(user.getUsername());
        toUpdate.setCodFiscale(user.getCodFiscale());
        toUpdate.setAddress(user.getAddress());
        toUpdate.setEmail(user.getEmail());

        if (!user.getPassword().startsWith("$2a")) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String newPassword = passwordEncoder.encode(user.getPassword());
            toUpdate.setPassword(newPassword);
        }

        toUpdate.setAvatar(user.getAvatar());

        return userRepository.save(toUpdate);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));;
        return user;
    }

    // METHODS

    @Transactional
    public String getAvatarFromUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        return user.getAvatar();
    }

    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {MailUserAlreadyExistsException.class, UsernameUserAlreadyExistsException.class})
    public User register(RegisterRequest registerRequest) throws MailUserAlreadyExistsException, ParseException, UsernameUserAlreadyExistsException {
        System.out.println(registerRequest.getUsername());
        System.out.println(registerRequest.getEmail());
        if ( userRepository.existsByUsername(registerRequest.getUsername()) ) {
            throw new UsernameUserAlreadyExistsException();
        }

        if ( userRepository.existsByEmail(registerRequest.getEmail()) ) {
            throw new MailUserAlreadyExistsException();
        }

        // Creo un nuovo utente
        User user = new User();
        user.setName(registerRequest.getName());
        user.setSurname(registerRequest.getSurname());
        user.setUsername(registerRequest.getUsername());
        user.setCodFiscale(registerRequest.getCodiceFiscale());
        user.setBirthdayDate(new SimpleDateFormat("yyyy-MM-dd").parse(registerRequest.getBirthdayDate()));
        user.setRole(roleRepository.findByType("ROLE_USER").orElse(null));
        user.setEmail(registerRequest.getEmail());
        user.setAddress(registerRequest.getAddress());

        userRepository.save(user);
        return user;
    }
}
