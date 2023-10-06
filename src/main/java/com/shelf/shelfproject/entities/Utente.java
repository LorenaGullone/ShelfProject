package com.shelf.shelfproject.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;


@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name= "utente", schema = "database")
public class Utente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Basic
    @Column(name = "nome", nullable = true, length = 50)
    private String nome;

    @Basic
    @Column(name = "cognome", nullable = true, length = 50)
    private String lastName;

    @Basic
    @Column(name = "cellulare", nullable = true, length = 20)
    private String cellulare;

    @Basic
    @Column(name = "email" , nullable = true, length = 90)
    private String email;

    @Basic
    @Column(name = "indirizzo", nullable = true, length = 150)
    private String address;

    @Basic
    @Column(name = "avatar", nullable = true, length = 150)
    private String avatar;

    @Basic
    @Column(name = "cap", nullable = true, length = 150)
    private String cap;

    @OneToMany(mappedBy = "acquirente", cascade = CascadeType.MERGE)
    @JsonIgnore
    private Collection<Carrello> carrello;

    @Basic
    @Column(name = "admin")
    private boolean admin;

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

}//Utente


