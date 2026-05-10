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

            case MOTION -> {

                MotionSensorAvro motion =
                        (MotionSensorAvro) data;

                yield motion.getMotion() ? 1 : 0;
            }

            case SWITCH -> {

                SwitchSensorAvro sensor =
                        (SwitchSensorAvro) data;

                yield sensor.getState() ? 1 : 0;
            }

            case TEMPERATURE -> {

                if (data instanceof TemperatureSensorAvro t) {
                    yield t.getTemperatureC();
                }

                ClimateSensorAvro climate =
                        (ClimateSensorAvro) data;

                yield climate.getTemperatureC();
            }

            case HUMIDITY -> {

                ClimateSensorAvro climate =
                        (ClimateSensorAvro) data;

                yield climate.getHumidity();
            }

            case CO2LEVEL -> {

                ClimateSensorAvro climate =
                        (ClimateSensorAvro) data;

                yield climate.getCo2Level();
            }

            case LUMINOSITY -> {

                LightSensorAvro light =
                        (LightSensorAvro) data;

                yield light.getLuminosity();
            }
        };
    }
}
