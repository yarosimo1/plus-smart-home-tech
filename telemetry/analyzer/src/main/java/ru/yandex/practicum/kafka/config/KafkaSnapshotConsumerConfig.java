package ru.yandex.practicum.kafka.config;

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

    @Bean(name = "kafkaSnapshotConsumer")
    public KafkaConsumer<String, SpecificRecordBase> kafkaSnapshotConsumer() {

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

        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);

        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);

        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);

        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);

        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        return new KafkaConsumer<>(props);
    }
}
