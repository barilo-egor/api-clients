package tgb.cryptoexchange.apiclients.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tgb.cryptoexchange.apiclients.dto.AuthRequest;
import tgb.cryptoexchange.apiclients.dto.AuthResponse;
import tgb.cryptoexchange.apiclients.dto.ClientDTO;
import tgb.cryptoexchange.apiclients.dto.TokenPair;
import tgb.cryptoexchange.apiclients.exceptions.UnauthorizedException;
import tgb.cryptoexchange.apiclients.service.AuthenticationManagerService;
import tgb.cryptoexchange.apiclients.service.ClientService;
import tgb.cryptoexchange.apiclients.service.CookieService;

@RestController
@Slf4j
@RequestMapping("/api-clients")
public class ClientController {

    private final CookieService cookieService;

    private final ClientService clientService;

    private final AuthenticationManagerService authService;

    public ClientController(ClientService clientService, AuthenticationManagerService authService,
            CookieService cookieService) {
        this.clientService = clientService;
        this.authService = authService;
        this.cookieService = cookieService;
    }

    @PostMapping("/clients")
    public ResponseEntity<ClientDTO> createClient(@RequestBody ClientDTO request) {
        ClientDTO savedClient = clientService.create(request);
        return ResponseEntity.ok().body(savedClient);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        if (authRequest == null || authRequest.username() == null || (authRequest.password() == null
                && authRequest.refreshToken() == null)) {
            throw new UnauthorizedException("No credentials provided");
        }
        TokenPair tokens = authService.authenticate(authRequest);

        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieService.createRefreshTokenCookie(tokens.refreshToken()).toString());
        return ResponseEntity.ok(new AuthResponse(tokens.accessToken()));
    }

}
