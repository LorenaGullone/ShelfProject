package com.shelf.shelfproject.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name= "user", schema = "database", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),@UniqueConstraint(columnNames = "username")})
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Basic
    @Column(name = "username", unique = true, length = 100, nullable = false)
    @NotNull
    private String username;

    @Basic
    @Column(name = "codice_fiscale", length = 20)
    @Size(min = 16, max = 16)
    @NotNull
    private String codFiscale;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "birthday_date")
    private Date birthdayDate;

    @Basic
    @Column(name = "password")
    @JsonIgnore
    private String password;

    @ManyToOne
    @JoinColumn(name = "role")
    private Role role;

    @Basic
    @Column(name = "name", length = 100)
    private String name;

    @Basic
    @Column(name = "surname", length = 100)
    private String surname;

    @Basic
    @NotNull
    @Email
    @Column(name = "email" , nullable = false, length = 90)
    private String email;

    @Basic
    @Column(name = "address", length = 150)
    private String address;

    @Basic
    @Column(name = "avatar", length = 150)
    private String avatar;


    //ogni utente può afferire a più acquisti (correnti o passati): relazione one to many (one User many Purchase)
    //la relazione è mantenuta da User e deve essere mappata in Purchase tramite il campo 'buyer' che corrisponde alla JoinColumn
    @OneToMany(mappedBy = "buyer", cascade = CascadeType.MERGE)
    @JsonIgnore
    private Collection<Purchase> purchases;

    //la relazione è mantenuta da Cart e deve essere mappata in User tramite il campo 'related_cart' che corrisponde alla JoinColumn
    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinColumn(name = "related_cart")
    @JsonIgnore
    private Cart cart;

    public User() {

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getType()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}//Utente


