package tgb.cryptoexchange.apiclients.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;
import tgb.cryptoexchange.apiclients.dto.WithdrawalRequestDTO;
import tgb.cryptoexchange.apiclients.exceptions.BodyMappingException;

@Slf4j
public class WithdrawalRequestReceiveEventSerializer implements Serializer<WithdrawalRequestDTO> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, WithdrawalRequestDTO withdrawalRequestReceiveEvent) {
        try {
            if (withdrawalRequestReceiveEvent == null) {
                return new byte[0];
            }
            return objectMapper.writeValueAsBytes(withdrawalRequestReceiveEvent);
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации объекта для отправки в топик {}: {}", topic, withdrawalRequestReceiveEvent);
            throw new BodyMappingException("Error occurred while mapping merchantHistory", e);
        }
    }
}
