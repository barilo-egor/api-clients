package tgb.cryptoexchange.apiclients.controller;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;
import tgb.cryptoexchange.apiclients.dto.ClientByApiKeyDTO;
import tgb.cryptoexchange.apiclients.dto.ClientDTO;
import tgb.cryptoexchange.apiclients.mapper.ClientMapper;
import tgb.cryptoexchange.apiclients.service.ClientService;
import tgb.cryptoexchange.grpc.generated.*;

@GrpcService
@Slf4j
public class ClientGrpcService extends ClientsServiceGrpc.ClientsServiceImplBase {

    private final ClientMapper mapper;

    private final ClientService clientService;

    public ClientGrpcService(ClientMapper mapper, ClientService clientService) {
        this.mapper = mapper;
        this.clientService = clientService;
    }

    @Override
    public void createClient(CreateClientGrpc request, StreamObserver<CreateClientResponseGrpc> responseObserver) {
        ClientDTO clientDTO = mapper.toDTO(request);
        ClientDTO savedClient = clientService.create(clientDTO);
        responseObserver.onNext(mapper.createClientResponseGrpc(savedClient));
        responseObserver.onCompleted();
    }

    @Override
    public void getClientByApiKey(GetClientByApiKeyGrpc request,
                                  StreamObserver<GetClientByApiKeyResponseGrpc> responseObserver) {
        ClientByApiKeyDTO clientDTO = clientService.getClientByApiKey(request.getApiKey());
        responseObserver.onNext(mapper.getClientByApiKeyResponseGrpc(clientDTO));
        responseObserver.onCompleted();
    }

    @Override
    public void getClientById(GetClientByIdGrpc request,
                              StreamObserver<GetClientByIdResponseGrpc> responseObserver) {
        ClientDTO clientDTO = clientService.getClientById(request.getId());
        responseObserver.onNext(mapper.getClientByIdResponseGrpc(clientDTO));
        responseObserver.onCompleted();
    }

    @Override
    public void createSignature(CreateSignatureGrpc request,
                                StreamObserver<CreateSignatureResponseGrpc> responseObserver) {
        final String signature = clientService.createSignature(request.getClientId(), request.getData());
        responseObserver.onNext(CreateSignatureResponseGrpc.newBuilder()
                .setSignature(signature)
                .build());
        responseObserver.onCompleted();
    }

}
