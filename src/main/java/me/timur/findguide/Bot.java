package me.timur.findguide;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    Map<Long, UserProgress> userProgressMap = new HashMap<>();

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

//    public void onUpdatesReceived(List<Update> updates) {
//        for (Update update : updates) {
//            if (update.hasMessage()) {
//                handleIncomingMessage(update.getMessage());
//            } else if (update.hasCallbackQuery()) {
//                handleCallbackQuery(update.getCallbackQuery());
//            }
//        }
//    }

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
                } else if (progress.isSelectingRegion()) {
                    // Store the selected region and ask for the start date
                    progress.setRegion(message.getText());
                    sendCalendar(message.getChatId(), "Please select a start date:");
                    progress.setSelectingRegion(false);
                    progress.setSelectingStartDate(true);
                } else if (progress.isSelectingStartDate()) {
                    // Store the selected start date and ask for the end date
                    String startDate = message.getText();
                    progress.setStartDate(startDate);
                    sendCalendar(message.getChatId(), "Please select an end date:");
                    progress.setSelectingStartDate(false);
                    progress.setSelectingEndDate(true);
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

//    private String findGuide(String language, String region, String period, String endDate) {
//        // TODO: Implement the logic to find a guide with the given parameters
//        return "Sorry, I couldn't find a guide with the given parameters.";
//    }

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

    private void sendCalendar(long chatId, String messageText) {
        // Create a reply keyboard markup with a calendar
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        // Create a row for the "Cancel" button
        List<InlineKeyboardButton> cancelButtonRow = new ArrayList<>();
        final InlineKeyboardButton cancel = new InlineKeyboardButton("Cancel");
        cancel.setCallbackData("Cancel");
        cancelButtonRow.add(cancel);
        buttons.add(cancelButtonRow);
        // Create rows for the days of the month
        List<InlineKeyboardButton> dayButtonRow = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            final InlineKeyboardButton button = new InlineKeyboardButton(String.valueOf(i));
            button.setCallbackData(String.valueOf(i));
            dayButtonRow.add(button);
            if (i % 7 == 0) {
                buttons.add(dayButtonRow);
                dayButtonRow = new ArrayList<>();
            }
        }
        if (!dayButtonRow.isEmpty()) {
            buttons.add(dayButtonRow);
        }
        // Set the keyboard
        markup.setKeyboard(buttons);
        // Create the message with the calendar
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messageText);
        message.setReplyMarkup(markup);
        // Send the message with the calendar
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

  private void handleCallbackQuery(CallbackQuery query) {
        String data = query.getData();
        long chatId = query.getMessage().getChatId();
        UserProgress progress = userProgressMap.get(chatId);
        if (data.equals("Cancel")) {
          // Cancel the current operation and clear the user's progress
          sendMessage(chatId, "Operation cancelled.");
          userProgressMap.remove(chatId);
        } else if (progress.isSelectingLanguage()) {
          // Store the selected language and ask for the region
          progress.setLanguage(data);
          sendMessage(chatId, "Please select a region:", createRegionOptionsKeyboard());
          progress.setSelectingLanguage(false);
          progress.setSelectingRegion(true);
        } else if (progress.isSelectingRegion()) {
          // Store the selected region and ask for the start date
          progress.setRegion(data);
          sendCalendar(chatId, "Please select a start date:");
          progress.setSelectingRegion(false);
          progress.setSelectingStartDate(true);
        } else if (progress.isSelectingStartDate()) {
          // Store the selected start date and ask for the end date
          String startDate = String.format("%02d", Integer.parseInt(data));
          progress.setStartDate(startDate);
          sendCalendar(chatId, "Please select an end date:");
          progress.setSelectingStartDate(false);
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

    public class Guide {
        private String name;
        private String language;

        public Guide(String name, String language) {
            this.name = name;
            this.language = language;
        }

        public String getName() {
            return name;
        }

        public String getLanguage() {
            return language;
        }
    }

    public class UserProgress {
        private boolean selectingLanguage;
        private boolean selectingRegion;
        private boolean selectingStartDate;
        private boolean selectingEndDate;
        private String language;
        private String region;
        private String startDate;
        private String endDate;

        public UserProgress() {
            selectingLanguage = true;
        }

        public boolean isSelectingLanguage() {
            return selectingLanguage;
        }

        public void setSelectingLanguage(boolean selectingLanguage) {
            this.selectingLanguage = selectingLanguage;
        }

        public boolean isSelectingRegion() {
            return selectingRegion;
        }

        public void setSelectingRegion(boolean selectingRegion) {
            this.selectingRegion = selectingRegion;
        }

        public boolean isSelectingStartDate() {
            return selectingStartDate;
        }

        public void setSelectingStartDate(boolean selectingStartDate) {
            this.selectingStartDate = selectingStartDate;
        }

        public boolean isSelectingEndDate() {
            return selectingEndDate;
        }

        public void setSelectingEndDate(boolean selectingEndDate) {
            this.selectingEndDate = selectingEndDate;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }
    }


}
