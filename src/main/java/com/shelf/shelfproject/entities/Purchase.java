package com.shelf.shelfproject.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;


@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "purchase", schema = "database")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Basic
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "purchase_time")
    private Date timeStamp;

    //la relazione è mantenuta da User e deve essere mappata in Purchase tramite il campo 'acquirente' che corrisponde alla JoinColumn
    @ManyToOne(optional = false)
    @JoinColumn(name = "buyer")
    @NotNull
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private User buyer;

    //ogni ordine può contenere più dettagli ordine: relazione one to many (one Purchase many ProductInPurhcase)
    //la relazione è mantenuta da Purchase e deve essere mappata in ProductInPurchase tramite il campo 'ordine' che corrisponde alla JoinColumn
    @OneToMany(mappedBy = "purchase", cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.REFRESH})
    private List<ProductInPurchase> products;

}//Purchase
