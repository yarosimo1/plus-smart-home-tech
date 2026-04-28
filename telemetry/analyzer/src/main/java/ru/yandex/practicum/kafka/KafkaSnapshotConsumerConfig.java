package ru.yandex.practicum.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaSnapshotConsumerConfig {
    private final KafkaConfigProperties kafkaConfigProperties;

    @Bean
    public KafkaConsumer<String, SpecificRecordBase> kafkaConsumer() {

        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaConfigProperties.getBootstrapServers());

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                kafkaConfigProperties.getConsumer().getKeyDeserializer());

        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                kafkaConfigProperties.getConsumer().getSnapshot().getValueDeserializer());

        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                kafkaConfigProperties.getConsumer().getSnapshot().getGroupId());

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                kafkaConfigProperties.getConsumer().getEnableAutoCommit());

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                kafkaConfigProperties.getConsumer().getAutoOffsetReset());

        return new KafkaConsumer<>(props);
    }
}
