package ru.practicum.collector.hub.event.scenario;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ScenarioCondition {
    private String sensorId;
    @JsonProperty("type")
    private ConditionType conditionType;
    @JsonProperty("operation")
    private ConditionOperation conditionOperation;
    private Integer value;
}
