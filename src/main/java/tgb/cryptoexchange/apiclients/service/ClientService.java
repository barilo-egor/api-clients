package tgb.cryptoexchange.apiclients.service;

import com.google.protobuf.Any;
import com.google.rpc.BadRequest;
import com.google.rpc.Code;
import com.google.rpc.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tgb.cryptoexchange.apiclients.dto.ClientByApiKeyDTO;
import tgb.cryptoexchange.apiclients.dto.ClientDTO;
import tgb.cryptoexchange.apiclients.dto.GeneratedKeys;
import tgb.cryptoexchange.apiclients.entity.Client;
import tgb.cryptoexchange.apiclients.enums.ClientStatus;
import tgb.cryptoexchange.apiclients.exceptions.ClientAlreadyExistsException;
import tgb.cryptoexchange.apiclients.exceptions.GrpcBaseException;
import tgb.cryptoexchange.apiclients.exceptions.PasswordValidationException;
import tgb.cryptoexchange.apiclients.mapper.ClientMapper;
import tgb.cryptoexchange.apiclients.repository.ClientRepository;

@Service
@Slf4j
@Transactional
public class ClientService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    private final ClientRepository clientRepository;

    private final KeyManagementService keyManagementService;

    private final ClientMapper clientMapper;

    public ClientService(ClientRepository clientRepository, KeyManagementService keyManagementService,
                         ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.keyManagementService = keyManagementService;
        this.clientMapper = clientMapper;
    }

    public ClientDTO create(ClientDTO clientDTO) {
        if (clientRepository.existsByUsername(clientDTO.getUsername())) {
            throw new ClientAlreadyExistsException("Username is already taken.");
        }
        final String encryptedPassword = validateAndHashPassword(clientDTO.getPassword());
        Client client = Client.builder().username(clientDTO.getUsername()).password(encryptedPassword).build();
        GeneratedKeys generatedKeys = keyManagementService.generateApiSecret(client);
        client.setStatus(ClientStatus.ACTIVE);
        client = clientRepository.save(client);

        return clientMapper.createdClientToDTO(client, generatedKeys);
    }

    public ClientByApiKeyDTO getClientByApiKey(String apiKey) {
        String hashedApiKey;
        try {
            if (apiKey == null || apiKey.isBlank()) {
                throw createInvalidApiKeyException();
            }
            hashedApiKey = keyManagementService.getHashedApiKey(apiKey);
        } catch (GrpcBaseException e) {
            throw createInvalidApiKeyException();
        }

        Client client = clientRepository.findByApiKey(hashedApiKey)
                .orElseThrow(() -> new GrpcBaseException(Status.newBuilder()
                        .setCode(Code.NOT_FOUND_VALUE)
                        .setMessage("User not found.")
                        .build()));
        String decryptedSecret = keyManagementService.decryptAesGcm(client.getSecret());
        return clientMapper.getClientByApiKeyDTO(client, decryptedSecret);
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
        if (password == null || !password.matches(PASSWORD_PATTERN)) {
            throw new PasswordValidationException("Password must be at least 8 characters long, " +
                    "include uppercase, lowercase, numbers, and special characters.");
        }
        return passwordEncoder.encode(password);
    }


}
