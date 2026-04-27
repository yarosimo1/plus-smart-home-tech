package ru.yandex.practicum.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.AggregatorService;
import ru.yandex.practicum.kafka.config.KafkaConfigProperties;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private final KafkaConsumer<String, SpecificRecordBase> consumer;
    private final KafkaProducer<String, SpecificRecordBase> producer;
    private final AggregatorService aggregatorService;
    private final KafkaConfigProperties topics;

    /**
     * Запуск процесса агрегации
     */
    public void start() {

        // hook для корректной остановки приложения
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown signal received");
            consumer.wakeup();
        }));

        try {
            log.info("Подписка на топик: {}", topics.getTopics().getSensors());
            consumer.subscribe(List.of(topics.getTopics().getSensors()));

            while (true) {
                var records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    SensorEventAvro event = (SensorEventAvro) record.value();

                    try {
                        Optional<SensorsSnapshotAvro> updated =
                                aggregatorService.updateState(event);

                        if (updated.isPresent()) {
                            SensorsSnapshotAvro snapshot = updated.get();

                            producer.send(new ProducerRecord<>(
                                    topics.getTopics().getSnapshot(),
                                    event.getHubId(),   // ключ = hubId (важно для порядка)
                                    snapshot
                            ));
                        }

                    } catch (Exception e) {
                        log.error("Ошибка обработки события: {}", event, e);
                    }
                }

                // фиксируем offset только после обработки
                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }

        } catch (WakeupException ignored) {
            log.info("Consumer shutdown requested");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий", e);
        } finally {

            try {
                log.info("Флашим продюсер...");
                producer.flush();

                log.info("Фиксируем оффсеты...");
                consumer.commitSync();

            } catch (Exception e) {
                log.error("Ошибка при завершении", e);
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();

                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }
}
