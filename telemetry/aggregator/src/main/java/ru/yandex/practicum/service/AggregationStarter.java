package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final KafkaAggregationRunner kafkaAggregationRunner;

    public void start() {

        log.info("Starting aggregation service");

        kafkaAggregationRunner.run();

        log.info("Aggregation service stopped");
    }
}
