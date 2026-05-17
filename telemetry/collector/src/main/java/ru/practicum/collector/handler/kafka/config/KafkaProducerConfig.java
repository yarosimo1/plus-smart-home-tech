package ru.practicum.collector.handler.kafka.config;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaConfigProperties kafkaConfigProperties;

    @Bean
    public KafkaProducer<String, SpecificRecordBase> kafkaProducer() {

        Properties props = new Properties();

        props.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaConfigProperties.getBootstrapServers()
        );

        props.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                kafkaConfigProperties.getProducer().getKeySerializer()
        );

        props.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                kafkaConfigProperties.getProducer().getValueSerializer()
        );

        props.put(
                ProducerConfig.ACKS_CONFIG,
                "all"
        );

        props.put(
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
                true
        );

        props.put(
                ProducerConfig.RETRIES_CONFIG,
                10
        );

        props.put(
                ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,
                5
        );

        return new KafkaProducer<>(props);
    }
}