package tgb.cryptoexchange.apiclients.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * @see tgb.cryptoexchange.apiclients.entity.ClientRefreshToken
 */
@Data
@Builder
public class ClientRefreshTokenDTO {

    private final String token;

    private final Long clientId;

    private final Instant expiresAt;

}
