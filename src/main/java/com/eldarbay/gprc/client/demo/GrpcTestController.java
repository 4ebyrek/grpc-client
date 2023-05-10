package com.eldarbay.gprc.client.demo;

import com.eldarbay.grpc.server.demo.grpc.ErrorResponse;
import com.eldarbay.grpc.server.demo.grpc.Ping;
import com.eldarbay.grpc.server.demo.grpc.Pong;
import com.eldarbay.grpc.server.demo.grpc.SuperServiceGrpc;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class GrpcTestController {

    @Autowired
    private SuperServiceGrpc.SuperServiceBlockingStub superServiceBlockingStub;

    @GetMapping("/execute/grpc/call")
    public ResponseEntity<String> createParking() {
        log.debug("REST request to execute GRpc request");
        Ping ping = Ping.newBuilder()
                .setPhraseFromPing("ping")
                .setNestedPing(Ping.NestedPing.newBuilder().setMessage("salem").build())
                .build();
        try {
            Pong pong = superServiceBlockingStub.ping(ping);
            ResponseEntity<String> ok = ResponseEntity.ok(pong.getSuccessResponse().getPhraseFromPong());
            return ok;
        }catch (Exception e) {
            log.error("Something go wrong {}", e.getMessage());
            Metadata metadata = Status.trailersFromThrowable(e);
            ErrorResponse errorResponse = metadata.get(ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance()));
            log.error("error from server = {}", errorResponse.getMessage());
            throw new RuntimeException();
        }
    }
}
