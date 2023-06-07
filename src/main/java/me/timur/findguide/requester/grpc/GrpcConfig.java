package me.timur.findguide.requester.grpc;

import com.proto.ProtoClientServiceGrpc;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import me.timur.findguide.requester.grpc.interceptor.TracingClientInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Temurbek Ismoilov on 04/05/23.
 */

@Slf4j
@Configuration
public class GrpcConfig {

    @Value("${grpc.server.url}")
    private String url;

    @Value("${grpc.server.port}")
    private Integer port;

    @Bean
    public ProtoClientServiceGrpc.ProtoClientServiceBlockingStub protoClientServiceBlockingStub() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(url, port)
                .usePlaintext()
                .build();

        // Create a blocking stub using the channel
        return ProtoClientServiceGrpc
                .newBlockingStub(channel)
                .withInterceptors(new TracingClientInterceptor()); // add any interceptors needed
    }
}
