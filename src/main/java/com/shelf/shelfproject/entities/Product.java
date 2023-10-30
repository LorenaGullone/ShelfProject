package com.shelf.shelfproject.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
@Table(name = "product", schema = "database", uniqueConstraints = {
        @UniqueConstraint(columnNames = "bar_code")})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Basic
    @NotNull
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Basic
    @NotNull
    @Column(name = "bar_code", nullable = false, length = 70)
    private String barCode;

    @Basic
    @Column(name = "description", length = 500)
    private String description;

    @Basic
    @Column(name = "image", length = 500)
    //String con URL relativo all'immagine del Product
    private String image;

    @Basic
    @Column(name = "style", length = 500)
    private Style style;

    @Basic
    @Column(name = "category", length = 500)
    private Category category;

    @Basic
    @PositiveOrZero
    @Column(name = "price", nullable = false)
    private float price;

    @Basic
    @Column(name = "quantity")
    @PositiveOrZero
    private int quantity;

    @Version
    @Column(name = "version", nullable = false)
    @JsonIgnore
    private long version;

    //ogni prodotto può essere contenuto in più dettagli ordine: relazione one to many (one Product many ProductInPurhcase)
    //la relazione è mantenuta da Product e deve essere mappata in ProductInPurchase tramite il campo 'prodotto' che corrisponde alla JoinColumn
    @OneToMany(targetEntity = ProductInPurchase.class, mappedBy = "product", cascade = CascadeType.MERGE)
    @JsonIgnore
    @ToString.Exclude
    private List<ProductInPurchase> inPurchases;

}//Product
