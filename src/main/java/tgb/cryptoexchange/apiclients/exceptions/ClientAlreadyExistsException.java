package tgb.cryptoexchange.apiclients.exceptions;

import lombok.Getter;
import tgb.cryptoexchange.apiclients.enums.ErrorCode;

@Getter
public class ClientAlreadyExistsException extends RuntimeException implements CustomException{

    private final ErrorCode errorCode;

    private final String field;

    private final String description;

    public ClientAlreadyExistsException() {
        super("Bad request.");
        this.errorCode = ErrorCode.INVALID_ARGUMENT;
        this.field = "username";
        this.description = "Username is already taken.";
    }

}
