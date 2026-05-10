package tgb.cryptoexchange.apiclients.exceptions;

public class ClientAlreadyExistsException extends RuntimeException{

    public ClientAlreadyExistsException(final String message) {
        super(message);
    }

}
