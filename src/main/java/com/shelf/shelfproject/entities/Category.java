package com.shelf.shelfproject.entities;

import com.shelf.shelfproject.support.exceptions.CategoryNotFoundException;

public enum Category {
    Bagno, Letto;

    public static Category convert(String string) throws CategoryNotFoundException {
        for( Category category : Category.values() ){
            if ( category.name().equalsIgnoreCase(string) )
                return category;
        }
        throw new CategoryNotFoundException();
    }

}//Category
