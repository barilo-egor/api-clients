package tgb.cryptoexchange.apiclients.mapper;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import tgb.cryptoexchange.apiclients.dto.ClientByApiKeyDTO;
import tgb.cryptoexchange.apiclients.dto.ClientDTO;
import tgb.cryptoexchange.apiclients.dto.GeneratedKeys;
import tgb.cryptoexchange.apiclients.entity.Client;
import tgb.cryptoexchange.grpc.generated.CreateClientGrpc;
import tgb.cryptoexchange.grpc.generated.CreateClientResponseGrpc;
import tgb.cryptoexchange.grpc.generated.GetClientByApiKeyResponseGrpc;

import java.time.Instant;

@Component
public class ClientMapper {

    public ClientDTO toDTO(CreateClientGrpc client) {
        return ClientDTO.builder().username(client.getUsername()).password(client.getPassword()).build();
    }

    public ClientDTO createdClientToDTO(Client client, GeneratedKeys generatedKeys) {
        return ClientDTO.builder()
                .username(client.getUsername())
                .apiKey(generatedKeys.key())
                .secret(generatedKeys.secret())
                .registeredAt(client.getRegisteredAt())
                .status(client.getStatus())
                .callbackUrl(client.getCallbackUrl())
                .build();
    }

    public CreateClientResponseGrpc dtoToGrpc(ClientDTO clientDTO) {
        return CreateClientResponseGrpc.newBuilder()
                .setUsername(clientDTO.getUsername())
                .setApiKey(clientDTO.getApiKey())
                .setSecret(clientDTO.getSecret())
                .setRegisteredAt(instantToTimestamp(clientDTO.getRegisteredAt()))
                .setStatus(clientDTO.getStatus().name())
                .setCallbackUrl(clientDTO.getCallbackUrl())
                .build();
    }

    public ClientByApiKeyDTO getClientByApiKeyDTO(Client client, String decryptedSecret) {
        return ClientByApiKeyDTO.builder()
                .username(client.getUsername())
                .secret(decryptedSecret)
                .status(client.getStatus())
                .build();
    }

    public GetClientByApiKeyResponseGrpc getClientByApiKeyResponseGrpc(ClientByApiKeyDTO clientDTO) {
        return GetClientByApiKeyResponseGrpc.newBuilder()
                .setUsername(clientDTO.getUsername())
                .setSecret(clientDTO.getSecret())
                .setSecret(clientDTO.getSecret())
                .build();
    }

    private Timestamp instantToTimestamp(Instant instant) {
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

}
