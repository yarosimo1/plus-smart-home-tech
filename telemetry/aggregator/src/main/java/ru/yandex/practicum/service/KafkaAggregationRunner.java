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

import java.time.Duration;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaAggregationRunner {

    private static final Duration POLL_TIMEOUT =
            Duration.ofSeconds(1);

    private final KafkaConsumer<String, SpecificRecordBase> consumer;
    private final KafkaConfigProperties properties;
    private final AggregationService aggregationService;

    private volatile boolean running = true;

    public void run() {

        consumer.subscribe(
                Collections.singletonList(
                        properties.getTopics().getSensors()
                )
        );

        log.info("Kafka consumer subscribed to topic: {}",
                properties.getTopics().getSensors());

        try {

            while (running) {

                ConsumerRecords<String, SpecificRecordBase> records =
                        consumer.poll(POLL_TIMEOUT);

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

    private void processRecord(
            ConsumerRecord<String, SpecificRecordBase> record
    ) {

        SensorEventAvro event =
                (SensorEventAvro) record.value();

        aggregationService.handle(event);
    }
}
