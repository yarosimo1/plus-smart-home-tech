package ru.yandex.practicum.service;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.ScenarioAction;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ActionExecutor {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub client;

    public ActionExecutor(
            @GrpcClient("hub-router")
            HubRouterControllerGrpc.HubRouterControllerBlockingStub client
    ) {
        this.client = client;
    }

    public void execute(SensorsSnapshotAvro snapshot, Scenario scenario) {
        for (ScenarioAction sa : scenario.getActions()) {

            if (sa.getAction() == null || sa.getSensor() == null) {
                continue;
            }

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

    public List<DeviceActionRequest> buildRequests(SensorsSnapshotAvro snapshot,
                                                   Scenario scenario) {
        log.info("BUILDING ACTIONS for scenario={}, actions={}",
                scenario.getName(),
                scenario.getActions().size());

        List<DeviceActionRequest> list = new ArrayList<>();

        for (ScenarioAction sa : scenario.getActions()) {

            log.info("ACTION -> sensorId={}, type={}, value={}",
                    sa.getSensor().getId(),
                    sa.getAction().getType(),
                    sa.getAction().getValue());

            DeviceActionRequest request =
                    DeviceActionRequest.newBuilder()
                            .setHubId(snapshot.getHubId())
                            .setScenarioName(scenario.getName())
                            .setTimestamp(toProto(snapshot.getTimestamp().toEpochMilli()))
                            .setAction(map(sa))
                            .build();

            list.add(request);
        }

        return list;
    }

    public void executeAll(List<DeviceActionRequest> requests) {
        log.info("SENDING TO HUB ROUTER: {} requests", requests.size());

        for (DeviceActionRequest request : requests) {
            try {
                log.info("gRPC CALL -> hubId={}, scenario={}",
                        request.getHubId(),
                        request.getScenarioName());

                client.handleDeviceAction(request);

                log.info("gRPC SENT SUCCESS");

            } catch (Exception e) {
                log.error("gRPC FAILED for request: {}", request, e);
            }
        }
    }

    private DeviceActionProto map(ScenarioAction sa) {
        return DeviceActionProto.newBuilder()
                .setSensorId(sa.getSensor().getId())
                .setType(ActionTypeProto.valueOf((sa.getAction().getType().name())))
                .setValue(sa.getAction().getValue())
                .build();
    }

    private Timestamp toProto(long millis) {
        return Timestamp.newBuilder()
                .setSeconds(millis / 1000)
                .setNanos((int) (millis % 1000) * 1_000_000)
                .build();
    }
}