package tgb.cryptoexchange.apiclients.exceptions;

import tgb.cryptoexchange.apiclients.enums.ErrorCode;

public interface CustomException {

    ErrorCode getErrorCode();

    String getField();

    String getDescription();

}
