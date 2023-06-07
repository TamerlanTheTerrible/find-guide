package me.timur.findguide.requester;

/**
 * Created by Temurbek Ismoilov on 04/05/23.
 */

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Method;
import com.proto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.timur.findguide.dto.RequestDto;
import me.timur.findguide.dto.UserDto;
import me.timur.findguide.exception.FindGuideBotException;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProtoClient implements RequesterService{
    private final ProtoClientServiceGrpc.ProtoClientServiceBlockingStub protoClientServiceBlockingStub;

    public UserDto getOrSaveUser(RequestDto request) {
        log.info("User get or save attempt: {}", request);
        var protoRequest = ProtoUserCreateDto.newBuilder()
                .setTelegramId(request.getChatId())
                .setTelegramUsername(request.getUsername() != null ? request.getUsername() : "")
                .setPhoneNumbers(request.getPhone() != null ? request.getPhone() : "")
                .setFirstName(request.getFirstName() != null ? request.getFirstName() : "")
                .setLastName(request.getLastName() != null ? request.getLastName() : "")
                .build();

        var protoUserDto = sendRequest(protoClientServiceBlockingStub::getOrSave, protoRequest, ProtoUserDto.class, "get or save telegramUser " + request.getChatId());
        return new UserDto(protoUserDto);
    }

    public UserDto getById(RequestDto request) {
        log.info("User get by id attempt: {}", request);
        final ProtoGetUserByIdRequest protoRequest = ProtoGetUserByIdRequest.newBuilder()
                .setId(request.getChatId())
                .build();

        var protoUserDto = sendRequest(protoClientServiceBlockingStub::getById, protoRequest, ProtoUserDto.class, "get user by id " + request.getChatId());
        return new UserDto(protoUserDto);
    }

    public UserDto getByTelegramId(RequestDto request) {
        log.info("User get by telegram id attempt: {}", request);
        final ProtoGetUserByTelegramIdRequest protoRequest = ProtoGetUserByTelegramIdRequest.newBuilder()
                .setTelegramId(request.getChatId())
                .build();

        var protoUserDto = sendRequest(protoClientServiceBlockingStub::getByTelegramId, protoRequest, ProtoUserDto.class, "get user by telegram id " + request.getChatId());
        return new UserDto(protoUserDto);
    }

    public <T extends Message, R extends Message> R sendRequest(Function<T, ProtoBaseResponse> requestMethod, T requestParam, Class<R> responseClass, String comment) {
        try {
            var protoResponse = requestMethod.apply(requestParam);
            return protoResponse.getPayload().unpack(responseClass);
        } catch (RuntimeException | InvalidProtocolBufferException e) {
            log.error("GRPC error while : {}, error: {}", comment, e.getMessage());
            throw new FindGuideBotException("GRPC error while : {}, error: {}", comment, e.getMessage());
        }
    }
}

