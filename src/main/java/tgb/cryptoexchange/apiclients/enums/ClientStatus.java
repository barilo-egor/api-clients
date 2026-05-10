package tgb.cryptoexchange.apiclients.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ClientStatus {

    ACTIVE("Активен"),BLOCKED("Заблокирован");

    private final String description;

}
