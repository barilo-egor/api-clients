package tgb.cryptoexchange.apiclients.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import tgb.cryptoexchange.apiclients.enums.ClientStatus;

@Data
@Builder
public class ClientByApiKeyDTO {

    private String username;

    @ToString.Exclude
    private String secret;

    private ClientStatus status;

}
