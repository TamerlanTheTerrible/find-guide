package me.timur.findguide.requester;

import me.timur.findguide.dto.RequestDto;
import me.timur.findguide.dto.UserDto;

/**
 * Created by Temurbek Ismoilov on 04/05/23.
 */

public interface RequesterService {
    UserDto getOrSaveUser(RequestDto request);

    UserDto getById(RequestDto request);

    UserDto getByTelegramId(RequestDto request);
}
