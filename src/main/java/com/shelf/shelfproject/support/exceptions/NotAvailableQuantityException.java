package com.shelf.shelfproject.support.exceptions;


public class NotAvailableQuantityException extends Exception {

    private String product;

    public String getName() {
        return product;
    }

    public NotAvailableQuantityException() {
        super();
    }
    public NotAvailableQuantityException(String product) {
        super();
        this.product = product;
    }
}
