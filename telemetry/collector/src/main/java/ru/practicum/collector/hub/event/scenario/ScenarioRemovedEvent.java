package ru.practicum.collector.hub.event.scenario;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.collector.hub.event.HubEvent;
import ru.practicum.collector.hub.event.HubEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioRemovedEvent extends HubEvent {
    String name;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED_EVENT;
    }
}