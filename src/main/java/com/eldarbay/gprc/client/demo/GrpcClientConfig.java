package com.eldarbay.gprc.client.demo;

import com.eldarbay.grpc.server.demo.grpc.SuperServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLException;
import java.io.File;

@Configuration
public class GrpcClientConfig {
    @Value("${grpc.server.host:localhost}")
    private String host;

    @Value("${grpc.server.port:6565}")
    private int port;

    @Bean
    public ManagedChannel managedChannel() throws SSLException {
        return NettyChannelBuilder.forAddress(host, port)
                .intercept(new GrpcLoggingInterceptor())
                .useTransportSecurity()
                .sslContext(GrpcSslContexts.forClient().trustManager(new File("proto_tls/server.crt")).build())
                .build();
    }

    @Bean
    public SuperServiceGrpc.SuperServiceBlockingStub helloWorldServiceBlockingStub() throws SSLException {
        return SuperServiceGrpc.newBlockingStub(managedChannel());
    }
}
