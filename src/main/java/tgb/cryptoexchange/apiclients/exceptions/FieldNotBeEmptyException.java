package tgb.cryptoexchange.apiclients.exceptions;

import lombok.Getter;

@Getter
public class FieldNotBeEmptyException extends RuntimeException{

    private final String field;

    public FieldNotBeEmptyException(final String field) {
        super("Should not be empty.");
        this.field = field;
    }

}
