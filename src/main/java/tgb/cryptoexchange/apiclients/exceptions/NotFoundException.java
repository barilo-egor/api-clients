package tgb.cryptoexchange.apiclients.exceptions;

import lombok.Getter;
import tgb.cryptoexchange.apiclients.enums.ErrorCode;

@Getter
public class NotFoundException extends RuntimeException implements CustomException {

    private final ErrorCode errorCode;

    private final String field;

    private final String description;

    public NotFoundException(final String field) {
        super("Bad request.");
        this.field = field;
        this.errorCode = ErrorCode.NOT_FOUND;
        this.description = "Record not found for the provided ID.";
    }

}
