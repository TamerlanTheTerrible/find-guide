package me.timur.findguide;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.timur.findguide.constant.Command;
import me.timur.findguide.constant.Language;
import me.timur.findguide.dto.GuideParams;
import me.timur.findguide.dto.RequestDto;
import me.timur.findguide.service.factory.CallbackHandlerFactory;
import me.timur.findguide.util.KeyboardUtil;
import me.timur.findguide.util.UpdateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Temurbek Ismoilov on 22/03/23.
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class Bot extends TelegramLongPollingBot {

    private final CallbackHandlerFactory callbackHandlerFactory;

    @Value("${bot.username}")
    private String BOT_NAME;
    @Value("${bot.token}")
    private String BOT_TOKEN;

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public String getBotUsername() {
        return this.BOT_NAME;
    }

    public static final Map<Long, GuideParams> userProgressMap = new ConcurrentHashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                handleIncomingMessage(update.getMessage());
            } else if (update.hasCallbackQuery()) {
                handleCallbackQuery(update.getCallbackQuery());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleIncomingMessage(Message message) {
        var callbackHandler = callbackHandlerFactory.get(message.getText());
        var methods = callbackHandler.handle(new RequestDto(message));
        try {
            for (BotApiMethod<? extends Serializable> method : methods) {
                execute(method);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleCallbackQuery(CallbackQuery query) {
        var prefix = UpdateUtil.getPrefix(query.getData());
        var callbackHandler = callbackHandlerFactory.get(Command.get(prefix));
        var messages = callbackHandler.handle(new RequestDto(query));
        try {
            for (BotApiMethod<? extends Serializable> message : messages) {
                execute(message);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(long chatId, String message, InlineKeyboardMarkup markup) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        sendMessage.setReplyMarkup(markup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
