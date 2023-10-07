package com.shelf.shelfproject.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "dettaglio_carrello", schema = "database")
public class DettaglioOrdine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "carrello")
    @JsonIgnore
    @ToString.Exclude
    private Ordine ordine;

    @Basic
    @Column(name = "quantità", nullable = true)
    private int quantità;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "prodotto")
    private Prodotto prodotto;

}//DettaglioCarrello
