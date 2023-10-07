package com.shelf.shelfproject.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
@Table(name = "prodotto", schema = "database")
public class Prodotto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Basic
    @Column(name = "nome", nullable = true, length = 50)
    private String nome;

    @Basic
    @Column(name = "bar_code", nullable = true, length = 70)
    private String barCode;

    @Basic
    @Column(name = "descrizione", nullable = true, length = 500)
    private String descrizione;

    @Basic
    @Column(name = "immagine", nullable = true, length = 500)
    private String immagine;

    @Basic
    @Column(name = "genere", nullable = true, length = 500)
    private String genere;

    @Basic
    @Column(name = "prezzo", nullable = true)
    private float prezzo;

    @Basic
    @Column(name = "quantità", nullable = true)
    private int quantità;

    @Version
    @Column(name = "version", nullable = false)
    @JsonIgnore
    private long version;

    @OneToMany(targetEntity = DettaglioOrdine.class, mappedBy = "prodotto", cascade = CascadeType.MERGE)
    @JsonIgnore
    @ToString.Exclude
    private List<DettaglioOrdine> dettagliOrdine;

}//Prodotto
