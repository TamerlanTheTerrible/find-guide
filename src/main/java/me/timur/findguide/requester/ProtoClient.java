package me.timur.findguide.requester;

/**
 * Created by Temurbek Ismoilov on 04/05/23.
 */

import com.google.protobuf.InvalidProtocolBufferException;
import com.proto.ProtoBaseResponse;
import com.proto.ProtoClientServiceGrpc;
import com.proto.ProtoUserCreateDto;
import com.proto.ProtoUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.timur.findguide.dto.RequestDto;
import me.timur.findguide.dto.UserDto;
import me.timur.findguide.exception.FindGuideBotException;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProtoClient implements RequesterService{
    private final ProtoClientServiceGrpc.ProtoClientServiceBlockingStub grpcBlockingStub;

    public UserDto save(RequestDto request) {
        ProtoUserCreateDto protoRequest = ProtoUserCreateDto.newBuilder()
                .setTelegramId(request.getChatId())
                .setTelegramUsername(request.getUsername() != null ? request.getUsername() : "")
                .setPhoneNumbers(request.getPhone() != null ? request.getPhone() : "")
                .build();

        try {
            ProtoBaseResponse protoResponse = grpcBlockingStub.save(protoRequest);
            var protoUserDto = protoResponse.getPayload().unpack(ProtoUserDto.class);
            return new UserDto(protoUserDto);
        } catch (RuntimeException | InvalidProtocolBufferException e) {
            log.error("GRPC error while saving user: {}", e.getMessage());
            throw new FindGuideBotException("GRPC error while saving user: " + e.getMessage());
        }
    }

    public UserDto getById(RequestDto request) {
//            final ProtoBaseResponse byId = grpcBlockingStub.getById(request);
        return null;
    }

    public UserDto getByTelegramId(RequestDto request) {
//        final ProtoBaseResponse byTelegramId = grpcBlockingStub.getByTelegramId(request);
        return null;
    }
}

