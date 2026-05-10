package ru.yandex.practicum.kafka.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaConfigProperties {
    private String bootstrapServers;

    private Topics topics = new Topics();
    private Consumer consumer = new Consumer();

    @Data
    public static class Consumer {
        private Snapshot snapshot = new Snapshot();
        private Hub hub = new Hub();

        private String keyDeserializer;
        private String enableAutoCommit;
        private String autoOffsetReset;
    }

    @Data
    public static class Snapshot {
        private String groupId;
        private String valueDeserializer;
    }

    @Data
    public static class Hub {
        private String groupId;
        private String valueDeserializer;
    }

    @Data
    public static class Topics {
        private String hub;
        private String snapshot;
    }
}
