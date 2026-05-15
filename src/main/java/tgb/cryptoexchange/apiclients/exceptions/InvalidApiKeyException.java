package tgb.cryptoexchange.apiclients.exceptions;

import lombok.Getter;
import tgb.cryptoexchange.apiclients.enums.ErrorCode;

@Getter
public class InvalidApiKeyException extends RuntimeException implements CustomException {

    private final ErrorCode errorCode;

    private final String field;

    private final String description;

    public InvalidApiKeyException() {
        super("User not found.");
        this.field = "apiKey";
        this.errorCode = ErrorCode.INVALID_ARGUMENT;
        this.description = "ApiKey is invalid.";
    }

}
