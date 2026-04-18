package ru.practicum.collector.handler.kafka.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaConfigProperties {
    private String bootstrapServers;

    private Producer producer = new Producer();
    private Topics topics = new Topics();

    @Data
    public static class Producer {
        private String keySerializer;
        private String valueSerializer;
    }

    @Data
    public static class Topics {
        private String sensors;
        private String hubs;
    }
}
