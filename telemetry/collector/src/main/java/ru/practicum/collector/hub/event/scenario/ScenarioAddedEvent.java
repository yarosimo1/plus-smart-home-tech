package ru.practicum.collector.hub.event.scenario;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.collector.hub.event.HubEvent;
import ru.practicum.collector.hub.event.HubEventType;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {
    private String name;
    private List<ScenarioConditionAvro> scenarioConditions;
    private List<DeviceActionAvro> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED_EVENT;
    }
}
