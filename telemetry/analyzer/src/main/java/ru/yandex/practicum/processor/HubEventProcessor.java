package ru.yandex.practicum.processor;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.config.KafkaConfigProperties;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.service.ScenarioService;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@Component
public class HubEventProcessor implements Runnable {
    private final KafkaConsumer<String, SpecificRecordBase> consumer;

    private final KafkaConfigProperties kafkaConfig;

    private final ScenarioService scenarioService;

    private volatile boolean running = true;

    public HubEventProcessor(
            @Qualifier("kafkaHubEventConsumer")
            KafkaConsumer<String, SpecificRecordBase> consumer,
            KafkaConfigProperties kafkaConfig,
            ScenarioService scenarioService
    ) {
        this.consumer = consumer;
        this.kafkaConfig = kafkaConfig;
        this.scenarioService = scenarioService;
    }

    @Override
    public void run() {

        consumer.subscribe(
                Collections.singleton(
                        kafkaConfig.getTopics().getHub()
                )
        );

        while (running) {

            var records = consumer.poll(Duration.ofSeconds(1));

            for (ConsumerRecord<String, SpecificRecordBase> record : records) {

                HubEventAvro event = (HubEventAvro) record.value();

                process(event);
            }

            consumer.commitSync();
        }
    }

    private void process(HubEventAvro event) {

        Object payload = event.getPayload();

        switch (payload) {

            case DeviceAddedEventAvro added -> scenarioService.addSensor(
                    added,
                    event.getHubId()
            );

            case DeviceRemovedEventAvro removed -> scenarioService.removeSensor(removed);

            case ScenarioAddedEventAvro added -> scenarioService.addScenario(
                    added,
                    event.getHubId()
            );

            case ScenarioRemovedEventAvro removed -> scenarioService.removeScenario(
                    removed,
                    event.getHubId()
            );

            default -> log.warn("Unknown hub event {}", payload.getClass());
        }
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        consumer.wakeup();
        consumer.close();
    }
}
