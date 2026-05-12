package ru.yandex.practicum.grpc;

import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.entity.Action;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionExecutor {

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public void execute(
            String hubId,
            String scenarioName,
            String sensorId,
            Action action) {

        DeviceActionProto.Builder actionProto =
                DeviceActionProto.newBuilder()
                        .setSensorId(sensorId)
                        .setType(
                                ActionTypeProto.valueOf(
                                        action.getType().name()
                                )
                        );

        if (action.getValue() != null) {
            actionProto.setValue(action.getValue());
        }

        Instant now = Instant.now();

        DeviceActionRequest request =
                DeviceActionRequest.newBuilder()
                        .setHubId(hubId)
                        .setScenarioName(scenarioName)
                        .setAction(actionProto.build())
                        .setTimestamp(
                                Timestamp.newBuilder()
                                        .setSeconds(now.getEpochSecond())
                                        .setNanos(now.getNano())
                                        .build()
                        )
                        .build();

        try {
            hubRouterClient.handleDeviceAction(request);

            log.info(
                    "Action sent: hub={}, scenario={}, sensor={}",
                    hubId,
                    scenarioName,
                    sensorId
            );

        } catch (StatusRuntimeException e) {
            log.error(
                    "Failed to send action: hub={}, scenario={}, sensor={}, grpcStatus={}",
                    hubId,
                    scenarioName,
                    sensorId,
                    e.getStatus(),
                    e
            );
        }
    }
}