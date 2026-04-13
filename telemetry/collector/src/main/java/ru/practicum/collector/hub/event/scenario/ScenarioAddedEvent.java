package ru.practicum.collector.hub.event.scenario;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.collector.hub.event.HubEvent;
import ru.practicum.collector.hub.event.HubEventType;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {
    private String name;
    private List<ScenarioCondition> scenarioConditions;
    private List<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
