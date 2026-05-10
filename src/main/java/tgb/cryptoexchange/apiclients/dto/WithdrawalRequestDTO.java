package tgb.cryptoexchange.apiclients.dto;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import tgb.cryptoexchange.apiclients.enums.WithdrawalRequestStatus;

import java.time.Instant;

@Data
@Builder
public class WithdrawalRequestDTO {

    private Long id;

    private Long clientId;

    private Integer amount;

    private Instant createdAt;

    private WithdrawalRequestStatus status;

    private String wallet;

    private String comment;

}
