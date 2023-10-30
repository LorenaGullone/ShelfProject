package com.shelf.shelfproject.entities;

import com.shelf.shelfproject.support.exceptions.StyleNotFoundException;

public enum Style {
    Moderno, Retro;

    public static Style convert(String string) throws StyleNotFoundException {
        for( Style style : Style.values() ){
            if ( style.name().equalsIgnoreCase(string) )
                return style;
        }
        throw new StyleNotFoundException();
    }

}//Style
