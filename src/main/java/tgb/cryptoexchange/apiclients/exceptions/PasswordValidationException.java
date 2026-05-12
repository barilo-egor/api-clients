package tgb.cryptoexchange.apiclients.exceptions;

import lombok.Getter;

@Getter
public class PasswordValidationException extends RuntimeException{

    private final String field;

    public PasswordValidationException(final String message) {
        super(message);
        this.field = "password";
    }

}
