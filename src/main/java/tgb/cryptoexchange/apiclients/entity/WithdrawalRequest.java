package tgb.cryptoexchange.apiclients.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tgb.cryptoexchange.apiclients.enums.WithdrawalRequestStatus;

import java.time.Instant;

@Entity
@Data
@Builder
@Table(name = "widthdrawal_request")
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private Integer amount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WithdrawalRequestStatus status;

    @Column(nullable = false)
    private String wallet;

    @Column
    private String comment;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
