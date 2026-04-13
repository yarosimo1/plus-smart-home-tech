package ru.practicum.collector.hub.event.scenario;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

@Getter
@Setter
public class ScenarioCondition {
    private String sensorId;
    private ConditionTypeAvro type;
    private ConditionOperationAvro operation;
    private Integer value;
}
