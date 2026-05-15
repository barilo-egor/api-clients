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
import tgb.cryptoexchange.grpc.generated.GetClientByIdResponseGrpc;

import java.time.Instant;
import java.util.Objects;

@Component
public class ClientMapper {

    public ClientDTO toDTO(CreateClientGrpc client) {
        return ClientDTO.builder().username(client.getUsername()).password(client.getPassword()).build();
    }

    public ClientDTO createdClientToDTO(Client client, GeneratedKeys generatedKeys) {
        return ClientDTO.builder()
                .id(client.getId())
                .username(client.getUsername())
                .apiKey(generatedKeys.key())
                .secret(generatedKeys.secret())
                .registeredAt(client.getRegisteredAt())
                .status(client.getStatus())
                .callbackUrl(client.getCallbackUrl())
                .build();
    }

    public ClientDTO clientToDTO(Client client){
        return ClientDTO.builder()
                .id(client.getId())
                .username(client.getUsername())
                .password(client.getPassword())
                .apiKey(client.getApiKey())
                .apiKeyPreview(client.getApiKeyPreview())
                .secret(client.getSecret())
                .registeredAt(client.getRegisteredAt())
                .status(client.getStatus())
                .callbackUrl(client.getCallbackUrl())
                .orderTimeoutSeconds(client.getOrderTimeoutSeconds())
                .build();
    }

    public CreateClientResponseGrpc createClientResponseGrpc(ClientDTO clientDTO) {
        return CreateClientResponseGrpc.newBuilder()
                .setUsername(Objects.requireNonNullElse(clientDTO.getUsername(), ""))
                .setApiKey(Objects.requireNonNullElse(clientDTO.getApiKey(), ""))
                .setSecret(Objects.requireNonNullElse(clientDTO.getSecret(), ""))
                .setRegisteredAt(clientDTO.getRegisteredAt() != null
                        ? instantToTimestamp(clientDTO.getRegisteredAt())
                        : Timestamp.getDefaultInstance())
                .setStatus(clientDTO.getStatus() != null ? clientDTO.getStatus().name() : "")
                .setCallbackUrl(Objects.requireNonNullElse(clientDTO.getCallbackUrl(), ""))
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
                .setStatus(clientDTO.getStatus().name())
                .build();
    }

    public GetClientByIdResponseGrpc getClientByIdResponseGrpc(ClientDTO clientDTO) {
        return GetClientByIdResponseGrpc.newBuilder()
                .setId(clientDTO.getId())
                .setUsername(Objects.requireNonNullElse(clientDTO.getUsername(), ""))
                .setApiKeyPreview(Objects.requireNonNullElse(clientDTO.getApiKey(), ""))
                .setRegisteredAt(clientDTO.getRegisteredAt() != null
                        ? instantToTimestamp(clientDTO.getRegisteredAt())
                        : Timestamp.getDefaultInstance())
                .setStatus(clientDTO.getStatus() != null ? clientDTO.getStatus().name() : "")
                .setCallbackUrl(Objects.requireNonNullElse(clientDTO.getCallbackUrl(), ""))
                .setOrderTimeoutSeconds(clientDTO.getOrderTimeoutSeconds()!=null ? clientDTO.getOrderTimeoutSeconds() : 0)
                .build();
    }

    private Timestamp instantToTimestamp(Instant instant) {
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

}
