package me.timur.findguide.requester;

/**
 * Created by Temurbek Ismoilov on 04/05/23.
 */

public interface RequesterService {
    void save(Object request);

    void getById(Object request);

    void getByTelegramId(Object request);
}
