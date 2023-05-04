package me.timur.findguide.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.timur.findguide.service.BotApiMethodService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Temurbek Ismoilov on 04/05/23.
 */

@Slf4j
@Component
public class BotApiMethodServiceImpl implements BotApiMethodService {

    @Override
    public List<BotApiMethod<? extends Serializable>> sendMessage(long chatId, String message) {
        return List.of(new SendMessage(String.valueOf(chatId), message));
    }

    @Override
    public List<BotApiMethod<? extends Serializable>> sendMessage(long chatId, int prevMessageId, String message, InlineKeyboardMarkup markup) {
        DeleteMessage deleteMessage = new DeleteMessage(String.valueOf(chatId), prevMessageId);
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        sendMessage.setReplyMarkup(markup);
        return List.of(deleteMessage, sendMessage);
    }

    @Override
    public List<BotApiMethod<? extends Serializable>> sendMessage(long chatId, String message, InlineKeyboardMarkup markup) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        sendMessage.setReplyMarkup(markup);
        return List.of(sendMessage);
    }

}
