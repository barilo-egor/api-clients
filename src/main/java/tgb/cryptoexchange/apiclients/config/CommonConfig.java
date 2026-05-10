package tgb.cryptoexchange.apiclients.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import tgb.cryptoexchange.apiclients.dto.WithdrawalRequestDTO;
import tgb.cryptoexchange.apiclients.kafka.WithdrawalReceiveProducerListener;
import tgb.cryptoexchange.apiclients.kafka.WithdrawalRequestReceiveEventSerializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableAsync
public class CommonConfig {

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    @Profile("!kafka-disabled")
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    @Profile("!kafka-disabled")
    public ProducerFactory<String, WithdrawalRequestDTO> withdrawalRequestProducerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, WithdrawalRequestReceiveEventSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    @Profile("!kafka-disabled")
    public KafkaTemplate<String, WithdrawalRequestDTO> kafkaTemplate(WithdrawalReceiveProducerListener withdrawalReceiveProducerListener,
                                                                     KafkaProperties kafkaProperties) {
        KafkaTemplate<String, WithdrawalRequestDTO> kafkaTemplate = new KafkaTemplate<>(withdrawalRequestProducerFactory(kafkaProperties));
        kafkaTemplate.setProducerListener(withdrawalReceiveProducerListener);
        return kafkaTemplate;
    }


}
