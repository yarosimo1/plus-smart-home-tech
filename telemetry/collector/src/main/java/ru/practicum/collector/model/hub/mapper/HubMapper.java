package ru.practicum.collector.model.hub.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.collector.model.hub.event.scenario.DeviceAction;
import ru.practicum.collector.model.hub.event.scenario.ScenarioCondition;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;

@UtilityClass
public class HubMapper {
    public List<DeviceActionAvro> toDeviceActionAvro(List<DeviceAction> actions) {
        return actions.stream()
                .map(HubMapper::toDeviceActionAvro)
                .toList();
    }

    public List<ScenarioConditionAvro> toScenarioConditionAvro(List<ScenarioCondition> scenarioConditions) {
        return scenarioConditions.stream()
                .map(HubMapper::toScenarioConditionAvro)
                .toList();
    }

    private ScenarioConditionAvro toScenarioConditionAvro(ScenarioCondition cond) {

        ScenarioConditionAvro avro = new ScenarioConditionAvro();

        avro.setSensorId(cond.getSensorId());
        avro.setConditionType(ConditionTypeAvro.valueOf(cond.getConditionType().name()));
        avro.setOperation(ConditionOperationAvro.valueOf(cond.getConditionOperation().name()));
        avro.setValue(cond.getValue());

        return avro;
    }

    private DeviceActionAvro toDeviceActionAvro(DeviceAction action) {
        DeviceActionAvro avro = new DeviceActionAvro();

        avro.setSensorId(action.getSensorId());
        avro.setActionType(ActionTypeAvro.valueOf(action.getActionType().name()));
        avro.setValue(action.getValue());

        return avro;
    }
}