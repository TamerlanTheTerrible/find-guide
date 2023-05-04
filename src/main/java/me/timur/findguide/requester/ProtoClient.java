package me.timur.findguide.requester;

/**
 * Created by Temurbek Ismoilov on 04/05/23.
 */

import com.proto.ProtoBaseResponse;
import com.proto.ProtoGetUserByIdRequest;
import com.proto.ProtoGetUserByTelegramIdRequest;
import com.proto.ProtoUserCreateDto;
import com.proto.ProtoClientServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProtoClient {
    private final ProtoClientServiceGrpc.ProtoClientServiceBlockingStub grpcBlockingStub;

    public ProtoBaseResponse save(ProtoUserCreateDto request) {
        return grpcBlockingStub.save(request);
    }

    public ProtoBaseResponse getById(ProtoGetUserByIdRequest request) {
        return grpcBlockingStub.getById(request);
    }

    public ProtoBaseResponse getByTelegramId(ProtoGetUserByTelegramIdRequest request) {
        return grpcBlockingStub.getByTelegramId(request);
    }
}

