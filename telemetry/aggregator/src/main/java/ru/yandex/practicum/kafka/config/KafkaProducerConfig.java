package ru.yandex.practicum.kafka.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {
    private final KafkaConfigProperties kafkaConfigProperties;

    @Bean
    public KafkaProducer<String, SensorsSnapshotAvro> kafkaProducer() {

        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaConfigProperties.getBootstrapServers());

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                kafkaConfigProperties.getProducer().getKeySerializer());

        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                kafkaConfigProperties.getProducer().getValueSerializer());

        return new KafkaProducer<>(props);
    }
}