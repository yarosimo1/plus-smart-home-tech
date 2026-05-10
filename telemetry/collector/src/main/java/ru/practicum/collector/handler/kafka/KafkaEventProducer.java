package ru.practicum.collector.handler.kafka;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventProducer {

    private final KafkaProducer<String, SpecificRecordBase> producer;

    public void send(
            String topic,
            String key,
            SpecificRecordBase value
    ) {

        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(topic, key, value);

        try {

            producer.send(record).get();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to send kafka message",
                    e
            );
        }
    }

    @PreDestroy
    public void shutdown() {

        log.info("Closing kafka producer");

        producer.close();

        log.info("Kafka producer closed");
    }
}