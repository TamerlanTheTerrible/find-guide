package me.timur.findguide.requester;

import com.google.protobuf.InvalidProtocolBufferException;
import me.timur.findguide.dto.RequestDto;
import me.timur.findguide.dto.UserDto;

/**
 * Created by Temurbek Ismoilov on 04/05/23.
 */

public interface RequesterService {
    UserDto save(RequestDto request);

    UserDto getById(RequestDto request);

    UserDto getByTelegramId(RequestDto request);
}
