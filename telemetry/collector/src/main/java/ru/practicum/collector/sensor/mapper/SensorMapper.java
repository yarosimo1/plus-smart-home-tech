package ru.practicum.collector.sensor.mapper;

import lombok.experimental.UtilityClass;
import org.apache.avro.specific.SpecificRecordBase;
import ru.practicum.collector.sensor.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

@UtilityClass
public class SensorMapper {

    public SpecificRecordBase toAvro(SensorEvent event) {
        if (event instanceof LightSensorEvent light) {
            return toLightSensorAvro(light);
        }

        if (event instanceof ClimateSensorEvent climate) {
            return toClimateSensorAvro(climate);
        }

        if (event instanceof MotionSensorEvent motion) {
            return toMotionSensorAvro(motion);
        }

        if (event instanceof SwitchSensorEvent switchSensor) {
            return toSwitchSensorAvro(switchSensor);
        }

        if (event instanceof TemperatureSensorEvent temperatureSensor) {
            return toTemperatureSensorAvro(temperatureSensor);
        }

        throw new RuntimeException("Unknow event");
    }

    private LightSensorAvro toLightSensorAvro(LightSensorEvent event) {
        LightSensorAvro avro = new LightSensorAvro();
        avro.setLinkQuality(event.getLinkQuality());
        avro.setLuminosity(event.getLuminosity());

        return avro;
    }

    private ClimateSensorAvro toClimateSensorAvro(ClimateSensorEvent event) {
        ClimateSensorAvro avro = new ClimateSensorAvro();
        avro.setCo2Level(event.getCo2Level());
        avro.setHumidity(event.getHumidity());
        avro.setTemperatureC(event.getTemperatureC());

        return avro;
    }

    private MotionSensorAvro toMotionSensorAvro(MotionSensorEvent event) {
        MotionSensorAvro avro = new MotionSensorAvro();
        avro.setLinkQuality(event.getLinkQuality());
        avro.setMotion(event.isMotion());
        avro.setVoltage(event.getVoltage());

        return avro;
    }

    private SwitchSensorAvro toSwitchSensorAvro(SwitchSensorEvent event) {
        SwitchSensorAvro avro = new SwitchSensorAvro();
        avro.setState(event.isState());

        return avro;
    }

    private TemperatureSensorAvro toTemperatureSensorAvro(TemperatureSensorEvent event) {
        TemperatureSensorAvro avro = new TemperatureSensorAvro();
        avro.setId(event.getId());
        avro.setHubId(event.getHubId());
        avro.setTemperatureC(event.getTemperatureC());
        avro.setTemperatureF(event.getTemperatureF());
        avro.setTimestamp(event.getTimestamp());

        return avro;
    }


}