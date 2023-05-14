package me.timur.findguide.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.proto.ProtoBaseResponse;
import com.proto.ProtoUserCreateDto;
import com.proto.ProtoUserDto;
import lombok.Data;
import me.timur.findguide.util.LocalDateTimeUtil;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Temurbek Ismoilov on 30/04/23.
 */

@Data
public class UserDto implements Serializable {
    @NonNull
    @JsonProperty("id")
    private Long id;
    @JsonProperty("date_created")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = LocalDateTimeUtil.PATTERN) @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dateCreated;
    @JsonProperty("date_updated")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = LocalDateTimeUtil.PATTERN) @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dateUpdated;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("birth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = LocalDateTimeUtil.PATTERN) @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime birthDate;
    @JsonProperty("telegram_id")
    private Long telegramId;
    @JsonProperty("telegram_username")
    private String telegramUsername;
    @JsonProperty("phone_numbers")
    private List<String> phoneNumbers;
    @JsonProperty("is_active")
    private Boolean isActive;
    @JsonProperty("is_blocked")
    private Boolean isBlocked;

    public UserDto(ProtoUserDto protoUserDto) {
        this.id = protoUserDto.getId();
        this.telegramId = protoUserDto.getTelegramId();
        this.phoneNumbers = List.of(protoUserDto.getPhoneNumbers().split(""));
        this.isActive = protoUserDto.getIsActive();
        this.isBlocked = protoUserDto.getIsBlocked();
        this.telegramUsername = protoUserDto.getTelegramUsername();
        this.firstName = protoUserDto.getFirstName();
        this.lastName = protoUserDto.getLastName();
    }

    public boolean hasNameOrUsername() {
        return (firstName != null && !firstName.isBlank())
                || (lastName != null && !lastName.isBlank())
                || telegramUsername != null && !telegramUsername.isBlank();

    }

    public String getFullNameOrUsername() {
        if (hasNameOrUsername()) {
            return (firstName != null && !firstName.isBlank() ? firstName : "") + " " + (lastName != null && !lastName.isBlank() ? lastName : "");
        } else {
            return telegramUsername;
        }
    }
}
