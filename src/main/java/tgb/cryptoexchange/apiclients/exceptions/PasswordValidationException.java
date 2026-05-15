package tgb.cryptoexchange.apiclients.exceptions;

import lombok.Getter;
import tgb.cryptoexchange.apiclients.enums.ErrorCode;

@Getter
public class PasswordValidationException extends RuntimeException implements CustomException {

    private final ErrorCode errorCode;

    private final String field;

    private final String description;

    public PasswordValidationException() {
        super("Bad request.");
        this.errorCode = ErrorCode.INVALID_ARGUMENT;
        this.field = "password";
        this.description = "Password does not meet the requirements. It must be at least 8 characters long and include uppercase and lowercase letters, digits, and special characters.";
    }

}
