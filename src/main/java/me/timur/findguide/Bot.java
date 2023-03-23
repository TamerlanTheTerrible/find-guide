package me.timur.findguide;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppData;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

/**
 * Created by Temurbek Ismoilov on 22/03/23.
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String BOT_NAME;
    @Value("${bot.token}")
    private String BOT_TOKEN;

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            execute(test(update));
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public String getBotUsername() {
        return this.BOT_NAME;
    }

    private SendMessage test(Update update) {
        final SendMessage msg = new SendMessage();
        msg.setChatId(update.getMessage().getChatId());
        msg.setText("Hello");
        final InlineKeyboardButton button = new InlineKeyboardButton("hello from the other side");
        button.setWebApp(new WebAppInfo("https://github.com/revenkroz/telegram-web-app-bot-example"));
        final List<List<InlineKeyboardButton>> keyboard = List.of(
                List.of(button)
        );
        var markup = new InlineKeyboardMarkup(keyboard);
        msg.setReplyMarkup(markup);
        return msg;
    }

}
