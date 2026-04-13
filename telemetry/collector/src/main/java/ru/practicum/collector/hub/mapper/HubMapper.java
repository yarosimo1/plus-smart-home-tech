package ru.practicum.collector.hub.mapper;

import lombok.experimental.UtilityClass;
import org.apache.avro.specific.SpecificRecordBase;
import ru.practicum.collector.hub.event.HubEvent;
import ru.practicum.collector.hub.event.device.DeviceAddedEvent;
import ru.practicum.collector.hub.event.device.DeviceRemovedEvent;
import ru.practicum.collector.hub.event.scenario.ScenarioAddedEvent;
import ru.practicum.collector.hub.event.scenario.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@UtilityClass
public class HubMapper {
    public SpecificRecordBase toAvro(HubEvent event) {
        if (event instanceof DeviceAddedEvent deviceAddedEvent) {
            return toDeviceAddedEventAvro(deviceAddedEvent);
        }

        if (event instanceof DeviceRemovedEvent deviceRemovedEvent) {
            return toDeviceRemovedEventAvro(deviceRemovedEvent);
        }

        if (event instanceof ScenarioAddedEvent scenarioAddedEvent) {
            return toScenarioAddedEventAvro(scenarioAddedEvent);
        }

        if (event instanceof ScenarioRemovedEvent scenarioRemovedEvent) {
            return toScenarioRemovedEventAvro(scenarioRemovedEvent);
        }

        throw new RuntimeException("Unknow event");
    }

    private DeviceAddedEventAvro toDeviceAddedEventAvro(DeviceAddedEvent event) {
        DeviceAddedEventAvro avro = new DeviceAddedEventAvro();
        avro.setId(event.getId());
        avro.setType(event.getDeviceType());

        return avro;
    }

    private DeviceRemovedEventAvro toDeviceRemovedEventAvro(DeviceRemovedEvent event) {
        DeviceRemovedEventAvro avro = new DeviceRemovedEventAvro();
        avro.setId(event.getId());

        return avro;
    }

    private ScenarioAddedEventAvro toScenarioAddedEventAvro(ScenarioAddedEvent event) {
        ScenarioAddedEventAvro avro = new ScenarioAddedEventAvro();
        avro.setActions(event.getActions());
        avro.setConditions(event.getScenarioConditions());
        avro.setName(event.getName());

        return avro;
    }

    private ScenarioRemovedEventAvro toScenarioRemovedEventAvro(ScenarioRemovedEvent event) {
        ScenarioRemovedEventAvro avro = new ScenarioRemovedEventAvro();
        avro.setName(event.getName());

        return avro;
    }
}