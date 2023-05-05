package me.timur.findguide.dto;

import lombok.Getter;
import lombok.NonNull;
import me.timur.findguide.exception.FindGuideBotException;
import me.timur.findguide.util.UpdateUtil;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Created by Temurbek Ismoilov on 14/04/23.
 */

@Getter
public class RequestDto {
    private Long chatId;
    private String username;
    private String data;
    private Integer prevMessageId;
    private String phone;

    public RequestDto(@NonNull CallbackQuery query) {
        if (query == null) {
            throw new FindGuideBotException("CallbackQuery cannot be null");
        }
        this.chatId = query.getMessage().getChatId();
        this.username = query.getFrom().getUserName();
        this.data = UpdateUtil.getData(query.getData());
        this.prevMessageId = query.getMessage().getMessageId();
        this.phone = query.getMessage().getContact().getPhoneNumber();
    }

    public RequestDto(@NonNull Message message) {
        if (message == null) {
            throw new FindGuideBotException("Message cannot be null");
        }
        this.chatId = message.getChatId();
        this.username = message.getFrom().getUserName();
        this.data = message.getText();
        this.prevMessageId = message.getMessageId();
        if (message.getContact() != null) {
            this.phone = message.getContact().getPhoneNumber();

        }
    }
}
