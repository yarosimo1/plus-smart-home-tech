package ru.yandex.practicum;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AggregatorService {
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {

        // 1. Получаем или создаём снапшот
        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(
                event.getHubId(),
                hubId -> SensorsSnapshotAvro.newBuilder()
                        .setHubId(hubId)
                        .setTimestamp(event.getTimestamp())
                        .setSensorsState(new HashMap<>())
                        .build()
        );

        Map<String, SensorStateAvro> sensorsState = snapshot.getSensorsState();

        SensorStateAvro oldState = sensorsState.get(event.getId());

        // 2. Проверка на существующее состояние
        if (oldState != null) {

            // если событие старее — игнорируем
            if (oldState.getTimestamp().isAfter(event.getTimestamp())) {
                return Optional.empty();
            }

            // если payload не изменился — тоже игнорируем
            if (oldState.getData().equals(event.getPayload())) {
                return Optional.empty();
            }
        }

        // 3. Создаём новое состояние датчика
        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        // 4. Обновляем снапшот
        sensorsState.put(event.getId(), newState);
        snapshot.setTimestamp(event.getTimestamp());

        return Optional.of(snapshot);
    }
}