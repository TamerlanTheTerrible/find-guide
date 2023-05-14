package me.timur.findguide.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.timur.findguide.constant.Command;
import me.timur.findguide.dto.RequestDto;
import me.timur.findguide.dto.UserDto;
import me.timur.findguide.requester.RequesterService;
import me.timur.findguide.service.BotApiMethodService;
import me.timur.findguide.service.UpdateHandlerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Temurbek Ismoilov on 04/05/23.
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService implements UpdateHandlerService {

    private final RequesterService requesterService;
    private final BotApiMethodService botApiMethodService;


    @Override
    public List<BotApiMethod<? extends Serializable>> handle(RequestDto requestDto) {
        if (requestDto.getData().equals("/start")) {
            var user = requesterService.getOrSaveUser(requestDto);
            return botApiMethodService.sendMessage(
                    requestDto.getChatId(),
                    "Добро пожаловать" + (user.hasNameOrUsername() ? ", " + user.getFullNameOrUsername() : "")
            );
        }
        return null;
    }

    @Override
    public Command getType() {
        return Command.USER;
    }
}
