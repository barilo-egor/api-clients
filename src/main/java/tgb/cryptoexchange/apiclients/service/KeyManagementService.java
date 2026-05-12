package tgb.cryptoexchange.apiclients.service;

import com.google.rpc.Code;
import com.google.rpc.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.apiclients.dto.GeneratedKeys;
import tgb.cryptoexchange.apiclients.entity.Client;
import tgb.cryptoexchange.apiclients.exceptions.GrpcBaseException;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

@Service
@Slf4j
public class KeyManagementService {

    private final String masterKey;

    private static final String PREFIX = "tgb";

    private final SecureRandom secureRandom = new SecureRandom();

    public KeyManagementService(@Value("${secrets.master-key}") String masterKey) {
        this.masterKey = masterKey;
    }

    public GeneratedKeys generateApiSecret(Client client) {
        String body = generateRandomString();
        String checksum = calculateCrc32(body);
        String rawApiKey = PREFIX + "_" + body + "_" + checksum;

        client.setApiKey(hashSha256(rawApiKey));

        String preview = PREFIX + "_" + body.substring(0, 4) + "...." + checksum.substring(checksum.length() - 4);
        client.setApiKeyPreview(preview);

        byte[] rawSecret = new byte[32];
        secureRandom.nextBytes(rawSecret);
        String rawSecretForClient = Base64.getEncoder().encodeToString(rawSecret);
        client.setSecret(encryptAesGcm(rawSecret));
        return new GeneratedKeys(rawApiKey, rawSecretForClient);
    }

    public String getHashedApiKey(String apiKey) {
        return hashSha256(apiKey);
    }

    private String hashSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new GrpcBaseException(Status.newBuilder()
                    .setCode(Code.INTERNAL_VALUE)
                    .setMessage("Hash error")
                    .build());
        }
    }

    public String decryptAesGcm(String encryptedSecret) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedSecret);
            ByteBuffer bb = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[12];
            bb.get(iv);
            byte[] cipherText = new byte[bb.remaining()];
            bb.get(cipherText);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(masterKey.getBytes(StandardCharsets.UTF_8), "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);

            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
            byte[] decryptedBytes = cipher.doFinal(cipherText);
            return Base64.getEncoder().encodeToString(decryptedBytes);

        } catch (Exception e) {
            throw new GrpcBaseException(Status.newBuilder()
                    .setCode(Code.INTERNAL_VALUE)
                    .setMessage("Failed to decrypt secret")
                    .build());
        }
    }

    public String encryptAesGcm(byte[] data) {
        try {
            byte[] iv = new byte[12];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(masterKey.getBytes(StandardCharsets.UTF_8), "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
            byte[] cipherText = cipher.doFinal(data);

            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);
            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            log.error("Encryption operation failed. Check master key configuration.");
            throw new GrpcBaseException(Status.newBuilder()
                    .setCode(Code.INTERNAL_VALUE)
                    .setMessage("Encryption error")
                    .build());
        }
    }

    private String generateRandomString() {
        byte[] bytes = new byte[16];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes).substring(0, 16);
    }

    private String calculateCrc32(String input) {
        java.util.zip.CRC32 crc = new java.util.zip.CRC32();
        crc.update(input.getBytes(StandardCharsets.UTF_8));
        return Long.toHexString(crc.getValue());
    }

}