package tgb.cryptoexchange.apiclients.dto;

import lombok.Builder;
import lombok.Data;
import tgb.cryptoexchange.apiclients.enums.ClientStatus;

import java.time.Instant;

@Builder
@Data
public class ClientDTO {

    private final String username;

    private final String password;

    private final String apiKey;

    private final String secret;

    private Instant registeredAt;

    private ClientStatus status;

    private String callbackUrl;

}
