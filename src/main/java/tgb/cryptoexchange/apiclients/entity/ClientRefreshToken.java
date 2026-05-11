package tgb.cryptoexchange.apiclients.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "client_refresh_tokens")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ClientRefreshToken {

    @Id
    private UUID token;

    @Column(nullable = false, unique = true)
    private Long clientId;

    @Column(nullable = false)
    private Instant expiresAt;

}
