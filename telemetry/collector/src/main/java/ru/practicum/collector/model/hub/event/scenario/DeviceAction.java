package ru.practicum.collector.model.hub.event.scenario;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeviceAction {
    private String sensorId;
    @JsonProperty("type")
    private ActionType actionType;
    private Integer value;
}
