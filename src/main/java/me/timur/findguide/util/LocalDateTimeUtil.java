package me.timur.findguide.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Temurbek Ismoilov on 30/08/22.
 */

public class LocalDateTimeUtil {
    public final static String PATTERN = "yyyy-MM-dd HH:mm:ss";
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN);

    public static String toString(LocalDateTime dateTime){
        return dateTime.format(formatter);
    }

    public static LocalDateTime toLocalDateTime(String birthDate) {
        return LocalDateTime.parse(birthDate, formatter);
    }
}
