package ru.yandex.practicum.producer;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.config.KafkaConfigProperties;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

@Component
@RequiredArgsConstructor
public class SnapshotProducer {

    private final KafkaProducer<String, SpecificRecordBase> producer;
    private final KafkaConfigProperties properties;

    public void send(SensorsSnapshotAvro snapshot) {

        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(
                        properties.getTopics().getSnapshot(),
                        snapshot.getHubId(),
                        snapshot
                );

        producer.send(record);
    }
}
