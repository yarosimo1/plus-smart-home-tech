package ru.practicum.collector.sensor.mapper;

import lombok.experimental.UtilityClass;
import org.apache.avro.specific.SpecificRecordBase;
import ru.practicum.collector.sensor.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

@UtilityClass
public class SensorMapper {

    public SpecificRecordBase toAvro(SensorEvent event) {
        if (event instanceof LightSensorEvent light) {
            SensorEventAvro avro = new SensorEventAvro();
            avro.setHubId(event.getHubId());
            avro.setId(event.getId());
            avro.setTimestamp(event.getTimestamp());
            avro.setPayload(toLightSensorAvro(light));
            return avro;
        }

        if (event instanceof ClimateSensorEvent climate) {
            SensorEventAvro avro = new SensorEventAvro();
            avro.setHubId(event.getHubId());
            avro.setId(event.getId());
            avro.setTimestamp(event.getTimestamp());
            avro.setPayload(toClimateSensorAvro(climate));
            return avro;
        }

        if (event instanceof MotionSensorEvent motion) {
            SensorEventAvro avro = new SensorEventAvro();
            avro.setHubId(event.getHubId());
            avro.setId(event.getId());
            avro.setTimestamp(event.getTimestamp());
            avro.setPayload(toMotionSensorAvro(motion));
            return avro;
        }

        if (event instanceof SwitchSensorEvent switchSensor) {
            SensorEventAvro avro = new SensorEventAvro();
            avro.setHubId(event.getHubId());
            avro.setId(event.getId());
            avro.setTimestamp(event.getTimestamp());
            avro.setPayload(toSwitchSensorAvro(switchSensor));
            return avro;
        }

        if (event instanceof TemperatureSensorEvent temperatureSensor) {
            SensorEventAvro avro = new SensorEventAvro();
            avro.setHubId(event.getHubId());
            avro.setId(event.getId());
            avro.setTimestamp(event.getTimestamp());
            avro.setPayload(toTemperatureSensorAvro(temperatureSensor));
            return avro;
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