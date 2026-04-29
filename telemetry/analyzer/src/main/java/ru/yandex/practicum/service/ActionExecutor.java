package ru.yandex.practicum.service;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.ScenarioAction;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ActionExecutor {

    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub client;

    public void execute(SensorsSnapshotAvro snapshot, Scenario scenario) {

        for (ScenarioAction sa : scenario.getActions()) {

            DeviceActionRequest request =
                    DeviceActionRequest.newBuilder()
                            .setHubId(snapshot.getHubId())
                            .setScenarioName(scenario.getName())
                            .setTimestamp(toProto((snapshot.getTimestamp().toEpochMilli())))
                            .setAction(map(sa))
                            .build();

            client.handleDeviceAction(request);
        }
    }

    private DeviceActionProto map(ScenarioAction sa) {
        return DeviceActionProto.newBuilder()
                .setSensorId(sa.getSensor().getId())
                .setType(mapType(sa.getAction().getType()))
                .setValue(sa.getAction().getValue())
                .build();
    }

    private ActionTypeProto mapType(String type) {
        return ActionTypeProto.valueOf(type);
    }

    private Timestamp toProto(long millis) {
        return Timestamp.newBuilder()
                .setSeconds(millis / 1000)
                .setNanos((int) (millis % 1000) * 1_000_000)
                .build();
    }
}