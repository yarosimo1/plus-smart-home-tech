package ru.practicum.collector.handler.hub;

import org.springframework.stereotype.Component;
import ru.practicum.collector.handler.kafka.KafkaEventProducer;
import ru.practicum.collector.handler.kafka.config.KafkaConfigProperties;
import ru.practicum.collector.model.hub.mapper.HubMapper;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

@Component(value = "SCENARIO_ADDED")
public class ScenarioAddedHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {
    public ScenarioAddedHandler(KafkaEventProducer kafkaEventProducer, KafkaConfigProperties topics) {
        super(kafkaEventProducer, topics);
    }

    @Override
    public ScenarioAddedEventAvro mapToAvro(HubEventProto event) {
        ScenarioAddedEventProto _event = event.getScenarioAdded();
        return ScenarioAddedEventAvro.newBuilder()
                .setName(_event.getName())
                .setActions(HubMapper.toDeviceActionAvro(_event.getActionList()))
                .setConditions(HubMapper.toScenarioConditionAvro(_event.getConditionList()))
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }
}
