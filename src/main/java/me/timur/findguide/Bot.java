package me.timur.findguide;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.timur.findguide.constant.Command;
import me.timur.findguide.dto.RequestDto;
import me.timur.findguide.service.factory.CallbackHandlerFactory;
import me.timur.findguide.util.UpdateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.List;

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

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            handleIncomingMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    private void handleIncomingMessage(Message message) {
        log.info("BOT Message: {}", message.toString());
        var callbackHandler = callbackHandlerFactory.get(message.getText());
        var methods = callbackHandler.handle(new RequestDto(message));
        execute(methods);
    }

    private void handleCallbackQuery(CallbackQuery query) {
        log.info("BOT CallbackQuery: {}", query.toString());
        var prefix = UpdateUtil.getPrefix(query.getData());
        var callbackHandler = callbackHandlerFactory.get(Command.get(prefix));
        var methods = callbackHandler.handle(new RequestDto(query));
        execute(methods);
    }

    private void execute(List<BotApiMethod<? extends Serializable>> methods) {
        try {
            for (BotApiMethod<? extends Serializable> method : methods) {
                execute(method);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
