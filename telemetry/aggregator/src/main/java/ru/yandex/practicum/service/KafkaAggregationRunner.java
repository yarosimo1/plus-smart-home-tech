package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.config.KafkaConfigProperties;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.producer.SnapshotProducer;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaAggregationRunner {
    private final KafkaConsumer<String, SpecificRecordBase> consumer;
    private final KafkaConfigProperties properties;
    private final SnapshotService snapshotService;
    private final SnapshotProducer producer;
    private volatile boolean running = true;

    public void run() {
        consumer.subscribe(Collections.singletonList(properties.getTopics().getSensors()));

        log.info("Kafka consumer subscribed to topic: {}", properties.getTopics().getSensors());

        Duration pollTimeout = Duration.ofMillis(properties.getPollTimeoutMs());

        try {
            while (running) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(pollTimeout);

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    processRecord(record);
                }

                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }
        } catch (WakeupException e) {
            if (running) {
                throw e;
            }
        } catch (Exception e) {
            log.error("Error during aggregation polling", e);
        } finally {
            consumer.close();
            log.info("Kafka consumer closed");
        }
    }

    public void stop() {
        running = false;
        consumer.wakeup();
    }

    private void processRecord(ConsumerRecord<String, SpecificRecordBase> record) {
        SensorEventAvro event = (SensorEventAvro) record.value();

        snapshotService.updateState(event)
                .ifPresent(producer::send);
    }
}