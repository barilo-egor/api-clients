package tgb.cryptoexchange.apiclients.exceptions;

public class PasswordValidationException extends RuntimeException{

    public PasswordValidationException(final String message) {
        super(message);
    }

}
