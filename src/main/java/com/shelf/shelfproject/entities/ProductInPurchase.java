package com.shelf.shelfproject.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "product_in_purchase", schema = "database")
public class ProductInPurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    //la relazione è mantenuta da Purchase e deve essere mappata in ProductInPurchase tramite il campo 'ordine' che corrisponde alla JoinColumn
    @ManyToOne(optional = false)
    @JoinColumn(name = "related_purchase")
    @JsonIgnore
    @ToString.Exclude
    private Purchase purchase;

    //la relazione è mantenuta da Cart e deve essere mappata in ProductInPurchase tramite il campo 'carrello' che corrisponde alla JoinColumn
    @ManyToOne()
    @JoinColumn(name = "related_cart")
    @JsonIgnore
    private Cart cart;

    //la relazione è mantenuta da Product e deve essere mappata in ProductInPurchase tramite il campo 'prodotto' che corrisponde alla JoinColumn
    @ManyToOne()
    @JoinColumn(name = "product")
    @NotNull
    private Product product;

    @Basic
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Basic
    @Column(name = "price", nullable = false)
    private float price;

    @Version
    @Column(name = "version", nullable = false)
    @JsonIgnore
    private long version;


}//ProductInPurchase
