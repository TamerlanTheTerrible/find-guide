package me.timur.findguide.requester.grpc;

import com.proto.ProtoClientServiceGrpc;
import io.grpc.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Temurbek Ismoilov on 04/05/23.
 */

@Configuration
public class GrpcConfig {

    @Value("${grpc.server.port}")
    private String address;

    @Bean
    public ProtoClientServiceGrpc.ProtoClientServiceBlockingStub grpcBlockingStub() {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(address)
                .usePlaintext()
                .build();

        // Create a blocking stub using the channel
        return ProtoClientServiceGrpc.newBlockingStub(channel);
//                .withInterceptors(new ClientInterceptor() {
//                    @Override
//                    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
//                        return null;
//                    }
//                }); // add any interceptors needed
    }


}
