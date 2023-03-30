package me.timur.findguide.dto;

/**
 * Created by Temurbek Ismoilov on 28/03/23.
 */

public class Guide {
    private String name;
    private String language;

    public Guide(String name, String language) {
        this.name = name;
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public String getLanguage() {
        return language;
    }
}