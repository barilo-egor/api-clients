package tgb.cryptoexchange.apiclients.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringExclude;
import tgb.cryptoexchange.apiclients.enums.ClientStatus;

import java.time.Instant;

@Builder
@Data
public class ClientDTO {

    private final Long id;

    private final String username;

    @ToString.Exclude
    private final String password;

    @ToString.Exclude
    private final String apiKey;

    @ToString.Exclude
    private final String secret;

    private Instant registeredAt;

    private ClientStatus status;

    private String callbackUrl;

}
