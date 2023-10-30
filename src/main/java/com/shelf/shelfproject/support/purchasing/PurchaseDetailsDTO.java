package com.shelf.shelfproject.support.purchasing;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//Serve perchè si procede con un contratto di acquisto in base al prezzo e alla quantità che l'utente conosce
//Se ci sono cambiamenti, non il linea con il seguente DTO perché qualcun altro sta agendo sui prodotti nel carrello, l'acquisto fallisce


@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PurchaseDetailsDTO {

    private long id;
    private long product;
    private int quantity;
    private float unitPrice;


    public PurchaseDetailsDTO() {}

    public PurchaseDetailsDTO(int product, int quantity, float unitPrice, float totalPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;

    }

}
