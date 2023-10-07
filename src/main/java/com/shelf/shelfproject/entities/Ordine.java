package com.shelf.shelfproject.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;


@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "carrello", schema = "database")
public class Ordine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Basic
    @Column(name = "conferma")
    private boolean conferma;

    @Basic
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data")
    private Date data;

    @ManyToOne(optional = false)
    @JoinColumn(name = "acquirente")
    private Utente acquirente;

    @OneToMany(mappedBy = "carrello", cascade = CascadeType.MERGE)
    private List<DettaglioOrdine> dettagliOrdine;

}//Carrello
