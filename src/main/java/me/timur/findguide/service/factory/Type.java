package me.timur.findguide.service.factory;

/**
 * Created by Temurbek Ismoilov on 10/04/23.
 */

public interface Type<T extends Enum<T>> {
    T getType();
}
