package me.timur.findguide.service;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Temurbek Ismoilov on 04/05/23.
 */

public interface BotApiMethodService {
    List<BotApiMethod<? extends Serializable>> sendMessage(long chatId, String message);

    List<BotApiMethod<? extends Serializable>> sendMessage(long chatId, int prevMessageId, String message, InlineKeyboardMarkup markup);

    List<BotApiMethod<? extends Serializable>> sendMessage(long chatId, String message, InlineKeyboardMarkup markup);
}
