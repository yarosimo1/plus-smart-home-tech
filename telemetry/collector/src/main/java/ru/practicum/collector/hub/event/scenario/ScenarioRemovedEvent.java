package ru.practicum.collector.hub.event.scenario;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.collector.hub.event.HubEvent;
import ru.practicum.collector.hub.event.HubEventType;

import static ru.practicum.collector.hub.event.HubEventType.SCENARIO_REMOVED;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioRemovedEvent extends HubEvent {
    String name;

    @Override
    public HubEventType getType() {
        return SCENARIO_REMOVED;
    }
}