package me.timur.findguide.util;

/**
 * Created by Temurbek Ismoilov on 14/04/23.
 */

public class QueryDataUtil {
    public static final String DELIMINATOR = "-";

    public static String getPrefix(String rawData) {
        return rawData.split(DELIMINATOR)[0];
    }

    public static String getData(String rawData) {
        final String[] strings = rawData.split(DELIMINATOR);
        return strings.length == 0 ? rawData : strings[0];
    }
}
