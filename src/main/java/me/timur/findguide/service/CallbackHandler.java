package me.timur.findguide.service;

import me.timur.findguide.constant.CallbackPrefix;
import me.timur.findguide.dto.RequestDto;
import me.timur.findguide.service.factory.Type;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Temurbek Ismoilov on 14/04/23.
 */

public interface CallbackHandler extends Type<CallbackPrefix> {
    List<BotApiMethod<? extends Serializable>> handle(RequestDto requestDto);
}
