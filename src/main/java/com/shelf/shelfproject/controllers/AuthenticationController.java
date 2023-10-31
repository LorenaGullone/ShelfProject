package com.shelf.shelfproject.controllers;

import com.shelf.shelfproject.entities.User;
import com.shelf.shelfproject.payload.request.LoginRequest;
import com.shelf.shelfproject.payload.request.RegisterRequest;
import com.shelf.shelfproject.payload.response.JwtResponse;
import com.shelf.shelfproject.payload.response.MessageResponse;
import com.shelf.shelfproject.security.JwtUtils;
import com.shelf.shelfproject.services.UserService;
import com.shelf.shelfproject.support.exceptions.MailUserAlreadyExistsException;
import com.shelf.shelfproject.support.exceptions.UsernameUserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Value("${com.shelf.shelfproject.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${com.shelf.shelfproject.jwtRememberExpirationMs}")
    private int jwtRememberExpirationMs;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Date expiration =
                loginRequest.getRemember() ?
                        new Date((new Date()).getTime() + jwtRememberExpirationMs) :
                        new Date((new Date()).getTime() + jwtExpirationMs);

        String jwt = jwtUtils.generateJwtToken(authentication, expiration);

        User utente = (User) authentication.getPrincipal();
        List<String> roles = utente.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new JwtResponse(
                        jwt,
                        utente.getId(),
                        utente.getUsername(),
                        utente.getEmail(),
                        roles,
                        expiration
                )
        );

    }

    @GetMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        System.out.println(SecurityContextHolder.getContext().toString());
        SecurityContextHolder.clearContext();
        System.out.println(SecurityContextHolder.getContext().toString());
        return ResponseEntity.ok("Logout effettuato con successo!");
    }

    @PostMapping("/sign_up")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) throws ParseException {
        try {
            User user = userService.register(registerRequest);
            user.setPassword(encoder.encode(registerRequest.getPassword()));

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), registerRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            Date expiration = new Date((new Date()).getTime() + jwtExpirationMs);
            String jwt = jwtUtils.generateJwtToken(authentication, expiration);

            List<String> roles = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    new JwtResponse(
                            jwt,
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            roles,
                            expiration
                    )
            );
        } catch (MailUserAlreadyExistsException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        } catch (UsernameUserAlreadyExistsException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }
    }

}
