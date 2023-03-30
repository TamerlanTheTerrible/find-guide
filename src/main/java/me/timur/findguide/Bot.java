package me.timur.findguide;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.timur.findguide.dto.Guide;
import me.timur.findguide.dto.UserProgress;
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
import java.util.stream.Collectors;

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

    public static final Map<Long, UserProgress> userProgressMap = new ConcurrentHashMap<>();

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

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        for (Update update : updates) {
            if (update.hasMessage()) {
                handleIncomingMessage(update.getMessage());
            } else if (update.hasCallbackQuery()) {
                handleCallbackQuery(update.getCallbackQuery());
            }
        }
    }

    private void handleIncomingMessage(Message message) {
        if (message.hasText() && message.getText().startsWith("/findguide")) {
            // Ask for the language
            sendMessage(message.getChatId(), "Please select a language:", createLanguageOptionsKeyboard());
            // Store the chat ID to keep track of the user's progress
            userProgressMap.put(message.getChatId(), new UserProgress());
        } else {
            // Check if the user is currently selecting a language, region, or date
            UserProgress progress = userProgressMap.get(message.getChatId());
            if (progress != null) {
                if (progress.isSelectingLanguage()) {
                    // Store the selected language and ask for the region
                    progress.setLanguage(message.getText());
                    sendMessage(message.getChatId(), "Please select a region:",
                            createRegionOptionsKeyboard());
                    progress.setSelectingLanguage(false);
                    progress.setSelectingRegion(true);
                } else if (progress.isSelectingEndDate()) {
                    // Store the selected end date and search for a guide
                    String endDate = message.getText();
                    progress.setEndDate(endDate);
                    List<Guide> result = searchGuides(progress.getLanguage(), progress.getRegion(),
                            progress.getStartDate(), progress.getEndDate());
                    sendMessage(message.getChatId(), result.stream().map(Guide::getName).collect(Collectors.joining(",")));
                    // Clear the user's progress
                    userProgressMap.remove(message.getChatId());
                }
            }
        }
    }

    private void handleCallbackQuery(CallbackQuery query) {
        String data = query.getData();
        long chatId = query.getMessage().getChatId();
        int prevMessageId = query.getMessage().getMessageId();
        UserProgress progress = userProgressMap.get(chatId);
        if (data.equals("Cancel")) {
            // Cancel the current operation and clear the user's progress
            sendMessage(chatId, "Operation cancelled.");
            userProgressMap.remove(chatId);
        } else if (progress.isSelectingLanguage()) {
            // Store the selected language and ask for the region
            progress.setLanguage(data);
            sendMessage(chatId, prevMessageId,"Please select a region:", createRegionOptionsKeyboard());
            progress.setSelectingLanguage(false);
            progress.setSelectingRegion(true);
        } else if (progress.isSelectingRegion()) {
            // Store the selected region and ask for the start year
            progress.setRegion(data);
            sendYear(chatId, "Please select a start year:", prevMessageId);
            progress.setSelectingRegion(false);
            progress.setSelectingStartYear(true);
        }  else if (progress.isSelectingStartYear()) {
            // Store the selected year and ask for the start month
            progress.setRegion(data);
            sendMonth(chatId, "Please select a start month:", prevMessageId);
            progress.setSelectingStartYear(false);
            progress.setSelectingStartMonth(true);
        }  else if (progress.isSelectingStartMonth()) {
            // Store the selected moth and ask for the start date
            progress.setRegion(data);
            sendDay(chatId, "Please select a start date:", prevMessageId);
            progress.setSelectingStartMonth(false);
            progress.setSelectingStartDate(true);
        }  else if (progress.isSelectingStartDate()) {
            // Store the selected date and ask for the end year
            progress.setRegion(data);
            sendYear(chatId, "Please select a end year:", prevMessageId);
            progress.setSelectingStartDate(false);
            progress.setSelectingEndYear(true);
        }  else if (progress.isSelectingEndYear()) {
            // Store the selected end year and ask for the end month
            progress.setRegion(data);
            sendMonth(chatId, "Please select a end month:", prevMessageId);
            progress.setSelectingEndYear(false);
            progress.setSelectingEndMonth(true);
        } else if (progress.isSelectingEndMonth()) {
            // Store the selected end month and ask for the end date
            String startDate = data;
            progress.setStartDate(startDate);
            sendDay(chatId, "Please select an end date:", prevMessageId);
            progress.setSelectingEndMonth(false);
            progress.setSelectingEndDate(true);
        } else if (progress.isSelectingEndDate()) {
            // Store the selected end date and search
            String endDate = String.format("%02d", Integer.parseInt(data));
            progress.setEndDate(endDate);
            // Search for a guide based on the user's input
            String language = progress.getLanguage();
            String region = progress.getRegion();
            String startDateFormatted = formatDate(progress.getStartDate());
            String endDateFormatted = formatDate(progress.getEndDate());
            List<Guide> guides = searchGuides(language, region, startDateFormatted, endDateFormatted);
            if (guides.isEmpty()) {
                sendMessage(
                        chatId,
                        "No guides available for the selected criteria. "
                                + "Please try again with different criteria.");
                userProgressMap.remove(chatId);
            } else {
                // Display the search results to the user
                StringBuilder messageText = new StringBuilder();
                messageText.append("Here are the available guides:\n");
                for (Guide guide : guides) {
                    messageText
                            .append("\n- ")
                            .append(guide.getName())
                            .append(" (")
                            .append(guide.getLanguage())
                            .append(")");
                }
                sendMessage(chatId, messageText.toString());
                userProgressMap.remove(chatId);
            }
        }
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
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        var english = new InlineKeyboardButton("English");
        english.setCallbackData("English");
        buttons.add(english);
        var russian = new InlineKeyboardButton("Russian");
        russian.setCallbackData("Russian");
        buttons.add(russian);
        var german = new InlineKeyboardButton("German");
        german.setCallbackData("German");
        buttons.add(german);
        var italian = new InlineKeyboardButton("Italian");
        italian.setCallbackData("Italian");
        buttons.add(italian);
        keyboard.setKeyboard(Collections.singletonList(buttons));
        return keyboard;
    }

    private InlineKeyboardMarkup createRegionOptionsKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        final InlineKeyboardButton uzbekistan = new InlineKeyboardButton("Uzbekistan");
        uzbekistan.setCallbackData("Uzbekistan");
        buttons.add(uzbekistan);
        final InlineKeyboardButton tashkent = new InlineKeyboardButton("Tashkent");
        tashkent.setCallbackData("Tashkent");
        buttons.add(tashkent);
        keyboard.setKeyboard(Collections.singletonList(buttons));
        return keyboard;
    }

    private void sendYear(long chatId, String messageText, int prevMessageId) {
        // Create a reply keyboard markup with a calendar
        InlineKeyboardMarkup markup = createInlineKeyboard(List.of("2023","2024","2025"), 3);
        sendMessage(chatId, prevMessageId, messageText, markup);
    }

    private void sendMonth(long chatId, String messageText, int prevMessageId) {
        // Create a reply keyboard markup with a calendar
        List<String> months = List.of("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec");
        InlineKeyboardMarkup markup = createInlineKeyboard(months, 3);
        sendMessage(chatId, prevMessageId, messageText, markup);
    }

    private void sendDay(long chatId, String messageText, int prevMessageId) {
        // Create a reply keyboard markup with a calendar
        List<String> days = new ArrayList<>();
        for (int i=1; i <=31; i++){
            days.add(String.valueOf(i));
        }
        InlineKeyboardMarkup markup = createInlineKeyboard(days, 7);
        sendMessage(chatId, prevMessageId, messageText, markup);
    }

    private InlineKeyboardMarkup createInlineKeyboard(List<String> values, int rowLength) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> buttonRow = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton(String.valueOf(values.get(i)));
            button.setCallbackData(String.valueOf(i));
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

    private String formatDate(String date) {
        LocalDate localDate = LocalDate.of(2023, 1, Integer.parseInt(date));
        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private List<Guide> searchGuides(String language, String region, String startDate, String endDate) {
        // In a real application, this method would perform a search in a database or external API
        // Here, we just return a dummy list of guides
        List<Guide> guides = new ArrayList<>();
        if (language.equals("English") && region.equals("Tashkent") && startDate.equals("2023-03-27") && endDate.equals("2023-04-01")) {
            guides.add(new Guide("John", "English"));
            guides.add(new Guide("Sarah", "English"));
        } else if (language.equals("English") && region.equals("Samarkand-Bukhara") && startDate.equals("2023-03-30") && endDate.equals("2023-04-05")) {
            guides.add(new Guide("David", "English"));
            guides.add(new Guide("Emma", "English"));
        } else if (language.equals("Russian") && region.equals("Tashkent") && startDate.equals("2023-03-29") && endDate.equals("2023-04-03")) {
            guides.add(new Guide("Ivan", "Russian"));
            guides.add(new Guide("Olga", "Russian"));
        } else if (language.equals("Russian") && region.equals("Samarkand-Bukhara") && startDate.equals("2023-03-31") && endDate.equals("2023-04-06")) {
            guides.add(new Guide("Dmitry", "Russian"));
            guides.add(new Guide("Svetlana", "Russian"));
        } else if (language.equals("German") && region.equals("Tashkent") && startDate.equals("2023-03-28") && endDate.equals("2023-04-02")) {
            guides.add(new Guide("Hans", "German"));
            guides.add(new Guide("Greta", "German"));
        } else if (language.equals("German") && region.equals("Samarkand-Bukhara") && startDate.equals("2023-04-01") && endDate.equals("2023-04-07")) {
            guides.add(new Guide("Klaus", "German"));
            guides.add(new Guide("Ingrid", "German"));
        } else if (language.equals("Italian") && region.equals("Tashkent") && startDate.equals("2023-03-27") && endDate.equals("2023-04-01")) {
            guides.add(new Guide("Mario", "Italian"));
            guides.add(new Guide("Giulia", "Italian"));
        } else if (language.equals("Italian") && region.equals("Samarkand-Bukhara") && startDate.equals("2023-03-31") && endDate.equals("2023-04-06")) {
            guides.add(new Guide("Luigi", "Italian"));
            guides.add(new Guide("Maria", "Italian"));
        }
        return guides;
    }
}
