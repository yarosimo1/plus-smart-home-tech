package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.mapper.SensorStateMapper;
import ru.yandex.practicum.storage.SnapshotStateStore;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SnapshotService {

    private final SnapshotStateStore stateStore;
    private final SensorStateMapper mapper;

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {

        SensorsSnapshotAvro snapshot = stateStore.get(event.getHubId())
                .orElseGet(() -> createSnapshot(event));

        SensorStateAvro oldState =
                snapshot.getSensorsState().get(event.getId());

        // проверка timestamp
        if (oldState != null) {

            Instant oldTimestamp = oldState.getTimestamp();
            Instant newTimestamp = event.getTimestamp();

            if (oldTimestamp.isAfter(newTimestamp)) {
                return Optional.empty();
            }

            // дедупликация
            if (oldState.getData().equals(event.getPayload())) {
                return Optional.empty();
            }
        }

        SensorStateAvro newState = mapper.map(event);

        snapshot.getSensorsState()
                .put(event.getId(), newState);

        snapshot.setTimestamp(event.getTimestamp());

        stateStore.put(event.getHubId(), snapshot);

        return Optional.of(snapshot);
    }

    private SensorsSnapshotAvro createSnapshot(SensorEventAvro event) {

        Map<String, SensorStateAvro> sensorStateMap = new HashMap<>();

        SensorsSnapshotAvro snapshot = SensorsSnapshotAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setSensorsState(sensorStateMap)
                .build();

        stateStore.put(event.getHubId(), snapshot);

        return snapshot;
    }
}
