package me.timur.findguide.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Temurbek Ismoilov on 25/04/23.
 */

public class KeyboardUtil {

    public static InlineKeyboardMarkup createInlineKeyboard(List<String> values, int rowLength) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> buttonRow = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton(String.valueOf(values.get(i)));
            button.setCallbackData(values.get(i));
            buttonRow.add(button);
            if (i % rowLength == 0) {
                keyboard.add(buttonRow);
                buttonRow = new ArrayList<>();
            }
        }
        if (!buttonRow.isEmpty()) {
            keyboard.add(buttonRow);
        }

        return new InlineKeyboardMarkup(keyboard);
    }
}
