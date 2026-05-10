package ru.yandex.practicum.grpc.client;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubRouterClient {

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public void handleDeviceAction(DeviceActionRequest request) {

        try {

            hubRouterClient.handleDeviceAction(request);

            log.info(
                    "Action sent successfully: hubId={}, scenario={}",
                    request.getHubId(),
                    request.getScenarioName()
            );

        } catch (StatusRuntimeException e) {

            log.error(
                    "Failed to send device action: hubId={}, scenario={}",
                    request.getHubId(),
                    request.getScenarioName(),
                    e
            );
        }
    }
}
