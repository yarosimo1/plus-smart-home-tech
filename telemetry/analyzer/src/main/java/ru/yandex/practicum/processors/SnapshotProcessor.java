package ru.yandex.practicum.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.KafkaConfigProperties;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {
    private final KafkaConsumer<String, SpecificRecordBase> consumer;
    private final KafkaConfigProperties topics;

    public void start() {
        log.info("SnapshotProcessor started");

        consumer.subscribe(List.of(topics.getTopics().getSnapshot()));

        try {
            while (true) {
                var records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    SpecificRecordBase snapshot = record.value();

                    log.info("Получен снапшот: {}", snapshot);
                }

                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }

        } catch (Exception e) {
            log.error("Ошибка в SnapshotProcessor", e);
        } finally {
            consumer.close();
        }

    }
}
