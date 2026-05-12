package tgb.cryptoexchange.apiclients.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import tgb.cryptoexchange.apiclients.entity.ClientRefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface ClientRefreshTokenRepository
        extends JpaRepository<ClientRefreshToken, Long>, JpaSpecificationExecutor<ClientRefreshToken> {

    @Modifying
    @Query("DELETE FROM ClientRefreshToken t WHERE t.clientId = :clientId")
    void deleteByClientId(Long clientId);

    void deleteByToken(UUID token);

    Optional<ClientRefreshToken> findByToken(UUID token);

}
