package ru.yandex.practicum.kafka.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaConfigProperties {
    private String bootstrapServers;
    private Long pollTimeoutMs;

    private Producer producer = new Producer();
    private Topics topics = new Topics();
    private Consumer consumer = new Consumer();

    @Data
    public static class Producer {
        private String keySerializer;
        private String valueSerializer;
    }

    @Data
    public static class Consumer {
        private String groupId;
        private String keyDeserializer;
        private String valueDeserializer;
        private String enableAutoCommit;
        private String autoOffsetReset;
    }

    @Data
    public static class Topics {
        private String sensors;
        private String snapshot;
    }
}
