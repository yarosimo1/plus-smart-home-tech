package ru.practicum.collector.handler.hub;

import org.springframework.stereotype.Component;
import ru.practicum.collector.handler.kafka.KafkaEventProducer;
import ru.practicum.collector.handler.kafka.config.KafkaConfigProperties;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Component(value = "SCENARIO_REMOVED")
public class ScenarioRemovedHandlers extends BaseHubEventHandler<ScenarioRemovedEventAvro> {
    public ScenarioRemovedHandlers(KafkaEventProducer kafkaEventProducer, KafkaConfigProperties topics) {
        super(kafkaEventProducer, topics);
    }

    @Override
    public ScenarioRemovedEventAvro mapToAvro(HubEventProto event) {
        ScenarioRemovedEventProto _event = event.getScenarioRemoved();
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(_event.getName())
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }
}
