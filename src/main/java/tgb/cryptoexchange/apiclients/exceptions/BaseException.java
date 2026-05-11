package tgb.cryptoexchange.apiclients.exceptions;

public class BaseException extends RuntimeException{

    public BaseException(final String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

}
