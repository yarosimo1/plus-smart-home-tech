package ru.practicum.collector.handler.hub;

import org.springframework.stereotype.Component;
import ru.practicum.collector.handler.kafka.KafkaEventProducer;
import ru.practicum.collector.handler.kafka.config.KafkaConfigProperties;
import ru.practicum.collector.model.hub.event.HubEvent;
import ru.practicum.collector.model.hub.event.HubEventType;
import ru.practicum.collector.model.hub.event.scenario.ScenarioAddedEvent;
import ru.practicum.collector.model.hub.mapper.HubMapper;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

@Component(value = "SCENARIO_ADDED")
public class ScenarioAddedHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {
    public ScenarioAddedHandler(KafkaEventProducer kafkaEventProducer, KafkaConfigProperties topics) {
        super(kafkaEventProducer, topics);
    }

    @Override
    public ScenarioAddedEventAvro mapToAvro(HubEvent event) {
        ScenarioAddedEvent _event = (ScenarioAddedEvent) event;
        return ScenarioAddedEventAvro.newBuilder()
                .setName(_event.getName())
                .setActions(HubMapper.toDeviceActionAvro(_event.getActions()))
                .setConditions(HubMapper.toScenarioConditionAvro(_event.getScenarioConditions()))
                .build();
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
