package tgb.cryptoexchange.apiclients.controller;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.grpc.server.service.GrpcService;
import tgb.cryptoexchange.grpc.generated.GetPublicJWTKeyResponseGrpc;
import tgb.cryptoexchange.grpc.generated.SecurityServiceGrpc;

@GrpcService
@Slf4j
public class SecurityGrpcService extends SecurityServiceGrpc.SecurityServiceImplBase {

    private final String jwtPublicKey;

    public SecurityGrpcService(@Value("${secrets.jwt.public}") String jwtPublicKey) {
        this.jwtPublicKey = jwtPublicKey;
    }

    @Override
    public void getPublicKey(Empty request, StreamObserver<GetPublicJWTKeyResponseGrpc> responseObserver) {
        responseObserver.onNext(GetPublicJWTKeyResponseGrpc.newBuilder().setJwtKey(jwtPublicKey).build());
        responseObserver.onCompleted();
    }

}
