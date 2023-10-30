package com.shelf.shelfproject.support.exceptions;


public class PriceChangedException extends Exception {

    private final String product;

    public String getName() {
        return product;
    }

    public PriceChangedException(String product) {
        super();
        this.product = product;
    }

}
