package ru.yandex.practicum.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.config.KafkaConfigProperties;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProducer {
    private final KafkaProducer<String, SensorsSnapshotAvro> producer;
    private final KafkaConfigProperties properties;

    public void send(SensorsSnapshotAvro snapshot) {

        ProducerRecord<String, SensorsSnapshotAvro> record =
                new ProducerRecord<>(
                        properties.getTopics().getSnapshot(),
                        snapshot.getHubId(),
                        snapshot
                );

        producer.send(record, (RecordMetadata metadata, Exception exception) ->
                {
                    if (exception != null) {
                        log.error(
                                "Failed to send snapshot for hubId={}",
                                snapshot.getHubId(),
                                exception
                        );
                        return;
                    }

                    log.info(
                            "Snapshot sent successfully: topic={}, partition={}, offset={}, hubId={}",
                            metadata.topic(),
                            metadata.partition(),
                            metadata.offset(),
                            snapshot.getHubId()
                    );
                }
        );
    }
}