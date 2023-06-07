package me.timur.findguide.requester.grpc.interceptor;

/** Created by Temurbek Ismoilov on 07/06/23. */
import com.proto.ProtoBaseResponse;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;

/**
 * Interceptor for tracing all client incoming and outgoing calls to SLF4J.
 */
@Slf4j
public class TracingClientInterceptor implements ClientInterceptor {

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {

        log.info("GRPC method: {}", method.getBareMethodName());

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(io.grpc.ClientCall.Listener<RespT> responseListener, Metadata headers) {
                super.start(new ClientCall.Listener<RespT>() {
                    public void onMessage(RespT message) {
                        log.info("GRPC response => \n" + message);
                        responseListener.onMessage(message);
                    }
                }, headers);
            }

            @Override
            public void sendMessage(ReqT message) {
                log.info("grpc request => \n" + message);
                super.sendMessage(message);
            }
        };
    }
}
