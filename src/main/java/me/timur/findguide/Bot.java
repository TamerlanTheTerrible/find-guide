package me.timur.findguide;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.timur.findguide.constant.Language;
import me.timur.findguide.dto.Guide;
import me.timur.findguide.dto.GuideParams;
import me.timur.findguide.util.CalendarUtil;
import me.timur.findguide.util.KeyboardUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
        if (message.hasText() && message.getText().startsWith("/findguide")) {
            // Ask for the language
            sendMessage(message.getChatId(), "Please select a language:", createLanguageOptionsKeyboard());
            // Store the chat ID to keep track of the user's progress
            userProgressMap.put(message.getChatId(), new GuideParams());
        } else {
            // Check if the user is currently selecting a language, region, or date
            GuideParams progress = userProgressMap.get(message.getChatId());
        }
    }

    private void handleCallbackQuery(CallbackQuery query) {
    }

    private void sendMessage(long chatId, String message) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        try {
            execute(sendMessage);
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

    private void sendMessage(long chatId, int prevMessageId, String message, InlineKeyboardMarkup markup) {
        DeleteMessage deleteMessage = new DeleteMessage(String.valueOf(chatId), prevMessageId);
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        sendMessage.setReplyMarkup(markup);
        try {
            execute(deleteMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup createLanguageOptionsKeyboard() {
        List<String> languages = Arrays.stream(Language.values()).map(l -> l.text).toList();
        return KeyboardUtil.createInlineKeyboard(languages, 4);
    }
}
