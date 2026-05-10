package tgb.cryptoexchange.apiclients.dto;

import lombok.Builder;
import lombok.Data;
import tgb.cryptoexchange.apiclients.enums.ClientStatus;

@Data
@Builder
public class ClientByApiKeyDTO {

    private String username;

    private String secret;

    private ClientStatus status;

}
