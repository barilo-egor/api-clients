package tgb.cryptoexchange.apiclients.service.unit;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import tgb.cryptoexchange.apiclients.dto.GeneratedKeys;
import tgb.cryptoexchange.apiclients.entity.Client;
import tgb.cryptoexchange.apiclients.exceptions.BaseException;
import tgb.cryptoexchange.apiclients.exceptions.GrpcValidationException;
import tgb.cryptoexchange.apiclients.service.KeyManagementService;

@ExtendWith(MockitoExtension.class)
class KeyManagementServiceTest {

    @InjectMocks
    private KeyManagementService keyManagementService;

    @BeforeEach
    void setUp() {
        String testMasterKey = "1234567890121234";
        ReflectionTestUtils.setField(keyManagementService, "masterKey", testMasterKey);
    }

    @Test
    @DisplayName("Зашифрованные данные успешно расшифровываются обратно")
    void should_encryptAndDecryptSuccessfully_when_validDataProvided() {
        byte[] originalData = "secret-payload-data".getBytes(StandardCharsets.UTF_8);

        String encryptedBase64 = keyManagementService.encryptAesGcm(originalData);
        String decryptedBase64 = keyManagementService.decryptAesGcm(encryptedBase64);

        byte[] decryptedBytes = Base64.getDecoder().decode(decryptedBase64);
        String resultString = new String(decryptedBytes, StandardCharsets.UTF_8);

        assertNotNull(encryptedBase64);
        assertNotEquals(Base64.getEncoder().encodeToString(originalData), encryptedBase64);
        assertEquals("secret-payload-data", resultString);
    }

    @Test
    @DisplayName("Дешифрование падает с BaseException, если передан поврежденный шифротекст")
    void should_throwBaseException_when_encryptedTextIsCorrupted() {
        String corruptedCipher = Base64.getEncoder().encodeToString("bad-data-not-gcm-format".getBytes());

        assertThrows(BaseException.class, () ->
                keyManagementService.decryptAesGcm(corruptedCipher)
        );
    }

    @Test
    @DisplayName("Генерация secret корректно заполняет поля сущности Client и возвращает ключи")
    void should_populateClientFieldsAndReturnKeys_when_generatingSecret() {
        Client client = new Client();
        GeneratedKeys generatedKeys = keyManagementService.generateApiSecret(client);

        assertNotNull(generatedKeys);
        assertNotNull(generatedKeys.key());
        assertNotNull(generatedKeys.secret());

        String rawApiKey = generatedKeys.key();
        assertTrue(rawApiKey.startsWith("tgb_"));

        assertNotNull(client.getApiKey());
        assertEquals(64, client.getApiKey().length());

        assertNotNull(client.getApiKeyPreview());
        assertTrue(client.getApiKeyPreview().startsWith("tgb_"));
        assertTrue(client.getApiKeyPreview().contains("...."));

        assertNotNull(client.getSecret());

        String decryptedSecretFromClient = keyManagementService.decryptAesGcm(client.getSecret());
        assertEquals(generatedKeys.secret(), decryptedSecretFromClient);
    }

    @Test
    @DisplayName("Хэширование API-ключа возвращает корректную строку SHA-256 в hex формате")
    void should_returnCorrectSha256HexHash_when_apiKeyProvided() {
        String apiKey = "tgb_testkey_12345";
        String expectedHash = "0a2729db25cf27b5f1048bf5ef4e3b7d353b5f86b25edfdebc336a8e33a8702e";

        String resultHash = keyManagementService.hashSha256(apiKey);

        assertNotNull(resultHash);
        assertEquals(expectedHash, resultHash);
    }
}