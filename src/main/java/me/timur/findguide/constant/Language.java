package me.timur.findguide.constant;

import me.timur.findguide.exception.FindGuideBotException;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by Temurbek Ismoilov on 10/04/23.
 */

public enum Language {
    RUSSIAN("Русский"),
    ENGLISH("English"),
    GERMAN("Deutsch"),
    ITALIAN("Italiano"),
    SPANISH("Español");

    public final String text;

    Language(String text) {
        this.text = text;
    }

    public static Language get(String text){
        return Arrays.stream(Language.values())
                .filter(l -> Objects.equals(l.text, text))
                .findFirst()
                .orElseThrow(() -> new FindGuideBotException("Could not find language: %s", text));
    }
}
