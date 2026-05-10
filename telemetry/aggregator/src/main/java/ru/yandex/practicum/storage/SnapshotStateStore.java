package ru.yandex.practicum.storage;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SnapshotStateStore {

    @Getter
    private final Map<String, SensorsSnapshotAvro> snapshots = new ConcurrentHashMap<>();

    public Optional<SensorsSnapshotAvro> get(String hubId) {
        return Optional.ofNullable(snapshots.get(hubId));
    }

    public void put(String hubId, SensorsSnapshotAvro snapshot) {
        snapshots.put(hubId, snapshot);
    }
}