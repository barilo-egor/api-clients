package tgb.cryptoexchange.apiclients.exceptions;

public class UnauthorizedException extends RuntimeException{

    public UnauthorizedException(final String message) {
        super(message);
    }

}
