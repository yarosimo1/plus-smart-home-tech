package ru.practicum.collector.handler.kafka;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventProducer {
    private final KafkaProducer<String, SpecificRecordBase> producer;

    public KafkaEventProducer(KafkaProducer<String, SpecificRecordBase> producer) {
        this.producer = producer;
    }

    public void send(String topic, String key, SpecificRecordBase value) {
        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(topic, key, value);

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                exception.printStackTrace();
            }
        });
    }
}