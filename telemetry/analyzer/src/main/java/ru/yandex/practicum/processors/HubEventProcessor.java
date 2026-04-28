package ru.yandex.practicum.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.KafkaConfigProperties;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {
    private final KafkaConsumer<String, SpecificRecordBase> consumer;
    private final KafkaConfigProperties topics;

    @Override

    public void run() {
        log.info("HubEventProcessor started");

        consumer.subscribe(List.of(topics.getTopics().getHub()));

        try {
            while (true) {
                var records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {

                    SpecificRecordBase event = record.value();

                    log.info("Получено событие хаба: {}", event);
                }

                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }
        } catch (WakeupException ignored) {
            log.info("HubEventProcessor stopping...");
        } catch (Exception e) {
            log.error("Ошибка в HubEventProcessor", e);
        } finally {
            consumer.close();
        }
    }
}
