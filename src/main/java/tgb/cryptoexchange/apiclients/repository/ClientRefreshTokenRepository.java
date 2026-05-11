package tgb.cryptoexchange.apiclients.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tgb.cryptoexchange.apiclients.entity.ClientRefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface ClientRefreshTokenRepository
        extends JpaRepository<ClientRefreshToken, Long>, JpaSpecificationExecutor<ClientRefreshToken> {

    void deleteByClientId(Long clientId);

    void deleteByToken(UUID token);

    Optional<ClientRefreshToken> findByToken(UUID token);

}
