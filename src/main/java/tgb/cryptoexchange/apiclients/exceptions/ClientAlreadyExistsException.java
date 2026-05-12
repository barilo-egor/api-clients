package tgb.cryptoexchange.apiclients.exceptions;

import lombok.Getter;

@Getter
public class ClientAlreadyExistsException extends RuntimeException{

    private final String field;

    public ClientAlreadyExistsException(final String message) {
        super(message);
        this.field = "username";
    }

}
