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
        responseObserver.onNext(mapper.dtoToGrpc(savedClient));
        responseObserver.onCompleted();
    }

    @Override
    public void getClientByApiKey(GetClientByApiKeyGrpc request, StreamObserver<GetClientByApiKeyResponseGrpc> responseObserver) {
        ClientByApiKeyDTO clientDTO = clientService.getClientByApiKey(request.getApiKey());
        responseObserver.onNext(mapper.getClientByApiKeyResponseGrpc(clientDTO));
        responseObserver.onCompleted();
    }

}
