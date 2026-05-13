package tgb.cryptoexchange.apiclients.service;

import com.google.protobuf.Any;
import com.google.rpc.BadRequest;
import com.google.rpc.Code;
import com.google.rpc.Status;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tgb.cryptoexchange.apiclients.constants.Metrics;
import tgb.cryptoexchange.apiclients.dto.ClientByApiKeyDTO;
import tgb.cryptoexchange.apiclients.dto.ClientDTO;
import tgb.cryptoexchange.apiclients.dto.GeneratedKeys;
import tgb.cryptoexchange.apiclients.entity.Client;
import tgb.cryptoexchange.apiclients.enums.ClientStatus;
import tgb.cryptoexchange.apiclients.exceptions.ClientAlreadyExistsException;
import tgb.cryptoexchange.apiclients.exceptions.GrpcBaseException;
import tgb.cryptoexchange.apiclients.exceptions.NotFoundException;
import tgb.cryptoexchange.apiclients.exceptions.PasswordValidationException;
import tgb.cryptoexchange.apiclients.mapper.ClientMapper;
import tgb.cryptoexchange.apiclients.repository.ClientRepository;

@Service
@Slf4j
@Transactional
public class ClientService {

    private final PasswordEncoder passwordEncoder;

    private static final String STRENGTH_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    private final ClientRepository clientRepository;

    private final KeyManagementService keyManagementService;

    private final ClientMapper clientMapper;

    public ClientService(ClientRepository clientRepository, KeyManagementService keyManagementService,
            ClientMapper clientMapper, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.keyManagementService = keyManagementService;
        this.clientMapper = clientMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Timed(value = Metrics.CLIENT_CREATE, description = "Метрики запросов на создание client.")
    public ClientDTO create(ClientDTO clientDTO) {
        log.debug("Запрос на создание client: username {}", clientDTO.getUsername());
        if (clientRepository.existsByUsername(clientDTO.getUsername())) {
            throw new ClientAlreadyExistsException("Username is already taken.");
        }
        final String encryptedPassword = validateAndHashPassword(clientDTO.getPassword());
        Client client = Client.builder().username(clientDTO.getUsername()).password(encryptedPassword).build();
        GeneratedKeys generatedKeys = keyManagementService.generateApiSecret(client);
        client.setStatus(ClientStatus.ACTIVE);
        client = clientRepository.save(client);
        log.debug("Создан клиент client: {}", clientDTO);
        return clientMapper.createdClientToDTO(client, generatedKeys);
    }

    @Timed(value = Metrics.CLIENT_GET_BY_API_KEY, description = "Метрики запросов на получение client по apiKey.")
    public ClientByApiKeyDTO getClientByApiKey(String apiKey) {
        log.debug("Запрос client: apiKey {}", apiKey);
        String hashedApiKey;
        try {
            if (apiKey == null || apiKey.isBlank()) {
                throw createInvalidApiKeyException();
            }
            hashedApiKey = keyManagementService.hashSha256(apiKey);
        } catch (GrpcBaseException e) {
            throw createInvalidApiKeyException();
        }

        Client client = clientRepository.findByApiKey(hashedApiKey)
                .orElseThrow(() -> new GrpcBaseException(Status.newBuilder()
                        .setCode(Code.NOT_FOUND_VALUE)
                        .setMessage("User not found.")
                        .build()));
        log.debug("Найден client: id {}, apiKey {}, secret {}", client.getId(), client.getApiKey(), client.getSecret());
        String decryptedSecret = keyManagementService.decryptAesGcm(client.getSecret());
        return clientMapper.getClientByApiKeyDTO(client, decryptedSecret);
    }

    public ClientDTO getClientByUsername(String username) {
        log.debug("Запрос client: username {}", username);
        Client client = clientRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(username));
        return clientMapper.clientToDTO(client);
    }

    public ClientDTO getClientById(Long id) {
        log.debug("Запрос client: id {}", id);
        Client client = clientRepository.findClientById(id)
                .orElseThrow(() -> new NotFoundException(String.valueOf(id)));
        return clientMapper.clientToDTO(client);
    }

    private GrpcBaseException createInvalidApiKeyException() {
        throw new GrpcBaseException(
                Code.INVALID_ARGUMENT,
                "User not found.",
                Any.pack(BadRequest.newBuilder()
                        .addFieldViolations(BadRequest.FieldViolation.newBuilder()
                                .setField("apiKey")
                                .setDescription("ApiKey is invalid.")
                                .build())
                        .build())
        );
    }

    private String validateAndHashPassword(String password) {
        if (password == null || !password.matches(STRENGTH_REGEX)) {
            throw new PasswordValidationException("Password must be at least 8 characters long, " +
                    "include uppercase, lowercase, numbers, and special characters.");
        }
        return passwordEncoder.encode(password);
    }

}
