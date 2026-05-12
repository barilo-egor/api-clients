package tgb.cryptoexchange.apiclients.dto;

import lombok.*;
import tgb.cryptoexchange.apiclients.enums.ClientStatus;

import java.time.Instant;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {

    private Long id;

    private String username;

    @ToString.Exclude
    private String password;

    @ToString.Exclude
    private String apiKey;

    @ToString.Exclude
    private String secret;

    private Instant registeredAt;

    private ClientStatus status;

    private String callbackUrl;

}
