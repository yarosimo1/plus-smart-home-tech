package ru.yandex.practicum.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.entity.model.ConditionType;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Component
public class SensorStateExtractor {
    public Integer extractValue(
            SensorStateAvro state,
            ConditionType type
    ) {
        Object data = state.getData();

        return switch (type) {

            case MOTION -> extractMotionValue(data);

            case SWITCH -> extractSwitchValue(data);

            case TEMPERATURE -> extractTemperatureValue(data);

            case HUMIDITY -> extractHumidityValue(data);

            case CO2LEVEL -> extractCo2Value(data);

            case LUMINOSITY -> extractLuminosityValue(data);
        };
    }

    private Integer extractMotionValue(Object data) {
        if (data instanceof MotionSensorAvro motion) {
            return motion.getMotion() ? 1 : 0;
        }

        throw new IllegalArgumentException(
                "Expected MotionSensorAvro but got: "
                        + data.getClass().getSimpleName()
        );
    }

    private Integer extractSwitchValue(Object data) {
        if (data instanceof SwitchSensorAvro sensor) {
            return sensor.getState() ? 1 : 0;
        }

        throw new IllegalArgumentException(
                "Expected SwitchSensorAvro but got: "
                        + data.getClass().getSimpleName()
        );
    }

    private Integer extractTemperatureValue(Object data) {
        if (data instanceof TemperatureSensorAvro temperature) {
            return temperature.getTemperatureC();
        }

        if (data instanceof ClimateSensorAvro climate) {
            return climate.getTemperatureC();
        }

        throw new IllegalArgumentException(
                "Expected TemperatureSensorAvro or ClimateSensorAvro but got: "
                        + data.getClass().getSimpleName()
        );
    }

    private Integer extractHumidityValue(Object data) {

        if (data instanceof ClimateSensorAvro climate) {
            return climate.getHumidity();
        }

        throw new IllegalArgumentException(
                "Expected ClimateSensorAvro but got: "
                        + data.getClass().getSimpleName()
        );
    }

    private Integer extractCo2Value(Object data) {
        if (data instanceof ClimateSensorAvro climate) {
            return climate.getCo2Level();
        }

        throw new IllegalArgumentException(
                "Expected ClimateSensorAvro but got: "
                        + data.getClass().getSimpleName()
        );
    }

    private Integer extractLuminosityValue(Object data) {
        if (data instanceof LightSensorAvro light) {
            return light.getLuminosity();
        }

        throw new IllegalArgumentException(
                "Expected LightSensorAvro but got: "
                        + data.getClass().getSimpleName()
        );
    }
}