package ru.yandex.practicum.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.KafkaConfigProperties;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.service.ScenarioService;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class HubEventProcessor implements Runnable {
    private final KafkaConsumer<String, SpecificRecordBase> consumer;
    private final KafkaConfigProperties topics;
    private final ScenarioService scenarioService;

    public HubEventProcessor(
            @Qualifier("kafkaHubEventConsumer")
            KafkaConsumer<String, SpecificRecordBase> consumer,
            KafkaConfigProperties topics,
            ScenarioService scenarioService
    ) {
        this.consumer = consumer;
        this.topics = topics;
        this.scenarioService = scenarioService;
    }

    @Override
    public void run() {
        log.info("HubEventProcessor started");

        consumer.subscribe(List.of(topics.getTopics().getHub()));

        try {
            while (true) {
                var records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {

                    SpecificRecordBase event = record.value();

                    handleEvent(event);

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

    private void handleEvent(SpecificRecordBase value) {
        if (!(value instanceof HubEventAvro event)) {
            log.warn("Неизвестный тип события: {}", value.getClass());
            return;
        }

        Object payload = event.getPayload();
        String hubId = event.getHubId();

        try {
            switch (payload) {

                case DeviceAddedEventAvro e -> scenarioService.handleDeviceAdded(hubId, e);

                case DeviceRemovedEventAvro e -> scenarioService.handleDeviceRemoved(hubId, e);

                case ScenarioAddedEventAvro e -> scenarioService.handleScenarioAdded(hubId, e);

                case ScenarioRemovedEventAvro e -> scenarioService.handleScenarioRemoved(hubId, e);

                default -> log.warn("Неизвестный payload: {}", payload.getClass());
            }

        } catch (Exception ex) {
            log.error("Ошибка обработки события {}", event, ex);
        }
    }
}
