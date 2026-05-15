package tgb.cryptoexchange.apiclients.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.apiclients.dto.AuthRequest;
import tgb.cryptoexchange.apiclients.dto.ClientDTO;
import tgb.cryptoexchange.apiclients.dto.ClientRefreshTokenDTO;
import tgb.cryptoexchange.apiclients.dto.TokenPair;
import tgb.cryptoexchange.apiclients.enums.ErrorCode;
import tgb.cryptoexchange.apiclients.exceptions.UnauthorizedException;

import java.time.Instant;

@Service
@Slf4j
public class AuthenticationManagerService {

    private final JwtService jwtService;

    private final ClientService clientService;

    private final ClientRefreshTokenService tokenService;

    private final PasswordEncoder passwordEncoder;

    public AuthenticationManagerService(JwtService jwtService, ClientService clientService,
            ClientRefreshTokenService tokenService, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.clientService = clientService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Аутентифицирует клиента и генерирует пару токенов (Access/Refresh).
     * @param request данные аутентификации (username/password ИЛИ refreshToken)
     * @return новая пара токенов {@link TokenPair}
     * @throws UnauthorizedException если неверный пароль, токен не найден или просрочен
     */
    public TokenPair authenticate(AuthRequest request) {
        ClientDTO clientDTO;

        if (request.password() != null) {
            clientDTO = clientService.getClientByUsername(request.username());
            if (!passwordEncoder.matches(request.password(), clientDTO.getPassword())) {
                throw new UnauthorizedException("Invalid password");
            }
        } else {
            ClientRefreshTokenDTO dbToken = tokenService.findByToken(request.refreshToken())
                    .filter(t -> t.getExpiresAt().isAfter(Instant.now()))
                    .orElseThrow(() -> new UnauthorizedException("Invalid or expired refresh token"));
            clientDTO = clientService.getClientById(dbToken.getClientId());
        }
        String access = jwtService.generateAccessToken(clientDTO);
        String refreshToken = tokenService.createRefreshToken(clientDTO.getId());

        return new TokenPair(access, refreshToken);
    }

}
