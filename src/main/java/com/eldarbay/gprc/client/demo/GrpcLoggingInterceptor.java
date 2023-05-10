package com.eldarbay.gprc.client.demo;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GrpcLoggingInterceptor implements ClientInterceptor {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {

        ClientCall<ReqT, RespT> clientCall = next.newCall(method, callOptions);

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(clientCall) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                Listener<RespT> listener = new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {

                    @Override
                    public void onHeaders(Metadata headers) {
                        log.debug(String.format("Received headers for %s: %s", method.getFullMethodName(), headers));
                        super.onHeaders(headers);
                    }

                    @Override
                    public void onMessage(RespT message) {
                        log.debug(String.format("Received response message for %s: %s", method.getFullMethodName(), message));
                        super.onMessage(message);
                    }

                    @Override
                    public void onClose(Status status, Metadata trailers) {
                        log.debug(String.format("gRPC request for %s closed with status %s", method.getFullMethodName(), status.getCode()));
                        log.debug(String.format("Response trailers: %s", trailers));
                        super.onClose(status, trailers);
                    }
                };
                super.start(listener, headers);
            }
        };
    }
}
