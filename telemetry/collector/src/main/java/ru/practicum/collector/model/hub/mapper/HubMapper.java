package ru.practicum.collector.model.hub.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;

@UtilityClass
public class HubMapper {
    public List<DeviceActionAvro> toDeviceActionAvro(List<DeviceActionProto> actions) {
        return actions.stream()
                .map(HubMapper::toDeviceActionAvro)
                .toList();
    }

    public List<ScenarioConditionAvro> toScenarioConditionAvro(List<ScenarioConditionProto> scenarioConditions) {
        return scenarioConditions.stream()
                .map(HubMapper::toScenarioConditionAvro)
                .toList();
    }

    private static ScenarioConditionAvro toScenarioConditionAvro(ScenarioConditionProto cond) {

        ScenarioConditionAvro avro = new ScenarioConditionAvro();

        avro.setSensorId(cond.getSensorId());
        avro.setConditionType(ConditionTypeAvro.valueOf(cond.getType().name()));
        avro.setOperation(ConditionOperationAvro.valueOf(cond.getOperation().name()));

        if (cond.hasBoolValue()) {
            avro.setValue(cond.getBoolValue());
        }

        if (cond.hasIntValue()) {
            avro.setValue(cond.getIntValue());
        }

        return avro;
    }

    private static DeviceActionAvro toDeviceActionAvro(DeviceActionProto action) {
        DeviceActionAvro avro = new DeviceActionAvro();

        avro.setSensorId(action.getSensorId());
        avro.setActionType(ActionTypeAvro.valueOf(action.getType().name()));
        avro.setValue(action.getValue());

        return avro;
    }
}