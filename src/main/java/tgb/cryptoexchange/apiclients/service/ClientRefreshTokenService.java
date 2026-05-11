package tgb.cryptoexchange.apiclients.service;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.apiclients.dto.ClientRefreshTokenDTO;
import tgb.cryptoexchange.apiclients.entity.ClientRefreshToken;
import tgb.cryptoexchange.apiclients.exceptions.NotFoundException;
import tgb.cryptoexchange.apiclients.exceptions.UnauthorizedException;
import tgb.cryptoexchange.apiclients.repository.ClientRefreshTokenRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class ClientRefreshTokenService {

    private final ClientRefreshTokenRepository tokenRepository;

    private final Long refreshExpiration;

    private final TimeBasedEpochGenerator generator = Generators.timeBasedEpochGenerator();

    public ClientRefreshTokenService(ClientRefreshTokenRepository tokenRepository,
            @Value("${secrets.jwt.refresh-ttl-seconds}") Long refreshExpiration) {
        this.tokenRepository = tokenRepository;
        this.refreshExpiration = refreshExpiration;
    }

    public String createRefreshToken(Long clientId) {
        tokenRepository.deleteByClientId(clientId);
        ClientRefreshToken newToken = new ClientRefreshToken();
        newToken.setToken(generator.generate());
        newToken.setClientId(clientId);
        newToken.setExpiresAt(Instant.now().plusSeconds(refreshExpiration));

        return tokenRepository.save(newToken).getToken().toString();
    }

    public Optional<ClientRefreshTokenDTO> findByToken(String token){
        return tokenRepository.findByToken(UUID.fromString(token))
                .map(entity -> ClientRefreshTokenDTO.builder()
                        .token(entity.getToken().toString())
                        .clientId(entity.getClientId())
                        .expiresAt(entity.getExpiresAt())
                        .build());

    }

}
