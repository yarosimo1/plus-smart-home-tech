package ru.practicum.collector.hub.mapper;

import lombok.experimental.UtilityClass;
import org.apache.avro.specific.SpecificRecordBase;
import ru.practicum.collector.hub.event.HubEvent;
import ru.practicum.collector.hub.event.device.DeviceAddedEvent;
import ru.practicum.collector.hub.event.device.DeviceRemovedEvent;
import ru.practicum.collector.hub.event.scenario.DeviceAction;
import ru.practicum.collector.hub.event.scenario.ScenarioAddedEvent;
import ru.practicum.collector.hub.event.scenario.ScenarioCondition;
import ru.practicum.collector.hub.event.scenario.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.*;

@UtilityClass
public class HubMapper {
    public SpecificRecordBase toAvro(HubEvent event) {
        if (event instanceof DeviceAddedEvent deviceAddedEvent) {
            return toHubEventAvro(event, toDeviceAddedEventAvro(deviceAddedEvent));
        }

        if (event instanceof DeviceRemovedEvent deviceRemovedEvent) {
            return toHubEventAvro(event, toDeviceRemovedEventAvro(deviceRemovedEvent));
        }

        if (event instanceof ScenarioAddedEvent scenarioAddedEvent) {
            return toHubEventAvro(event, toScenarioAddedEventAvro(scenarioAddedEvent));
        }

        if (event instanceof ScenarioRemovedEvent scenarioRemovedEvent) {
            return toHubEventAvro(event, toScenarioRemovedEventAvro(scenarioRemovedEvent));
        }

        throw new RuntimeException("Unknow event");
    }

    private DeviceAddedEventAvro toDeviceAddedEventAvro(DeviceAddedEvent event) {
        DeviceAddedEventAvro avro = new DeviceAddedEventAvro();
        avro.setId(event.getId());
        avro.setDeviceType(DeviceTypeAvro.valueOf(event.getDeviceType().name()));

        return avro;
    }

    private DeviceRemovedEventAvro toDeviceRemovedEventAvro(DeviceRemovedEvent event) {
        DeviceRemovedEventAvro avro = new DeviceRemovedEventAvro();
        avro.setId(event.getId());

        return avro;
    }

    private ScenarioAddedEventAvro toScenarioAddedEventAvro(ScenarioAddedEvent event) {
        ScenarioAddedEventAvro avro = new ScenarioAddedEventAvro();
        avro.setName(event.getName());

        avro.setActions(
                event.getActions()
                        .stream()
                        .map(HubMapper::toDeviceActionAvro)
                        .toList()
        );

        avro.setConditions(
                event.getScenarioConditions()
                        .stream()
                        .map(HubMapper::toScenarioConditionAvro)
                        .toList()
        );
        return avro;
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

    private ScenarioRemovedEventAvro toScenarioRemovedEventAvro(ScenarioRemovedEvent event) {
        ScenarioRemovedEventAvro avro = new ScenarioRemovedEventAvro();
        avro.setName(event.getName());

        return avro;
    }

    private HubEventAvro toHubEventAvro(HubEvent event, Object value) {
        HubEventAvro avro = new HubEventAvro();
        avro.setHubId(event.getHubId());
        avro.setTimestamp(event.getTimestamp());
        avro.setPayload(value);

        return avro;
    }
}