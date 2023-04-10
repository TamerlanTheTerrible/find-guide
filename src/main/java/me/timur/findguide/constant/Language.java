package me.timur.findguide.constant;

/**
 * Created by Temurbek Ismoilov on 10/04/23.
 */

public enum Language {
    RUSSIAN("Русский"),
    ENGLISH("English"),
    GERMAN("Deutsch"),
    ITALIAN("Italiano"),
    SPANISH("Español");

    public final String value;

    Language(String value) {
        this.value = value;
    }
}
