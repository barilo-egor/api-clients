package tgb.cryptoexchange.apiclients.service.integration;

import com.google.protobuf.Empty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import tgb.cryptoexchange.grpc.generated.SecurityServiceGrpc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SecurityServiceIT extends BaseIntegrationTest{

    private SecurityServiceGrpc.SecurityServiceBlockingStub blockingStub;

    @Value("${secrets.jwt.public}")
    private String expectedPublicKey;

    @BeforeEach
    void initStub() {
        blockingStub = SecurityServiceGrpc.newBlockingStub(channel);
    }

    @Test
    @DisplayName("Должен возвращать корректный ключ из конфига")
    void getPublicKey_ShouldReturnConfiguredKey() {
        var request = Empty.getDefaultInstance();

        var response = blockingStub.getPublicKey(request);

        assertThat(response.getJwtKey()).isNotBlank();
        assertThat(response.getJwtKey()).isEqualTo(expectedPublicKey);
    }

}
