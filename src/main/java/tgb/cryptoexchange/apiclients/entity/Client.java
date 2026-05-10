package tgb.cryptoexchange.apiclients.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tgb.cryptoexchange.apiclients.enums.ClientStatus;

import java.time.Instant;

@Entity
@Data
@Builder
@Table(name = "clients")
@AllArgsConstructor
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "api_key", nullable = false)
    private String apiKey;

    @Column(name = "api_key_preview", nullable = false)
    private String apiKeyPreview;

    @Column(nullable = false)
    private String secret;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private Instant registeredAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClientStatus status;

    @Column(name = "callback_url")
    private String callbackUrl;

    @Builder.Default
    @Column(name = "order_timeout_seconds", nullable = false)
    private Integer orderTimeoutSeconds = 900;

    @PrePersist
    protected void onCreate() {
        if (registeredAt == null) {
            registeredAt = Instant.now();
        }
    }
}
