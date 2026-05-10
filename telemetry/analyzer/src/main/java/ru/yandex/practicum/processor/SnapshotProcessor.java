package ru.yandex.practicum.processor;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.config.KafkaConfigProperties;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.service.SnapshotAnalyzer;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class SnapshotProcessor {

    @Qualifier("kafkaSnapshotConsumer")
    private final KafkaConsumer<String, SpecificRecordBase> consumer;

    private final KafkaConfigProperties kafkaConfig;
    private final SnapshotAnalyzer snapshotAnalyzer;

    public SnapshotProcessor(
            @Qualifier("kafkaSnapshotConsumer")
            KafkaConsumer<String, SpecificRecordBase> consumer,
            KafkaConfigProperties kafkaConfig,
            SnapshotAnalyzer snapshotAnalyzer
    ) {
        this.consumer = consumer;
        this.kafkaConfig = kafkaConfig;
        this.snapshotAnalyzer = snapshotAnalyzer;
    }

    public void start() {

        consumer.subscribe(
                List.of(kafkaConfig.getTopics().getSnapshot())
        );

        log.info("Snapshot processor started");

        while (true) {

            ConsumerRecords<String, SpecificRecordBase> records =
                    consumer.poll(Duration.ofSeconds(1));

            for (ConsumerRecord<String, SpecificRecordBase> record : records) {

                try {

                    SensorsSnapshotAvro snapshot =
                            (SensorsSnapshotAvro) record.value();

                    snapshotAnalyzer.analyze(snapshot);

                } catch (Exception e) {

                    log.error(
                            "Failed to process snapshot record",
                            e
                    );
                }
            }

            consumer.commitSync();
        }
    }

    @PreDestroy
    public void shutdown() {

        log.info("Closing snapshot consumer");

        consumer.close();
    }
}