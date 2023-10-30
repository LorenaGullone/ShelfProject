package com.shelf.shelfproject.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "cart", schema = "database")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @JsonIgnore
    private long id;

    //ogni carrello ha un proprio e singolo utente di riferimento: relazione one to one
    //il vincolo è che l'utente deve essere un utente registrato
    //la relazione è mantenuta da Cart e deve essere mappata in User tramite il campo 'related_cart' che corrisponde alla JoinColumn
    @OneToOne(mappedBy = "cart", cascade = {CascadeType.REFRESH})
    @NotNull
    @JsonIgnore
    private User user;


    //ogni carrello ha una lista di dettaglio prodotti di riferimento: relazione one to many (one cart, many productInPurchase)
    //il vincolo è che ogni prodotto deve essere un prodotto reale
    //la relazione è mantenuta da Cart e deve essere mappata in ProductInPurchase tramite il campo 'carrello' che corrisponde alla JoinColumn
    @OneToMany(mappedBy = "cart", cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.REFRESH})
    private List<ProductInPurchase> products;

}//Cart
