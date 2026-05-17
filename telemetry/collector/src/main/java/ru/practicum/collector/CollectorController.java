package ru.practicum.collector;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.collector.handler.EventFactory;
import ru.practicum.collector.handler.hub.HubEventHandler;
import ru.practicum.collector.handler.sensor.SensorEventHandler;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CollectorController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final EventFactory eventFactory;

    @Override
    public void collectSensorEvent(
            SensorEventProto request,
            StreamObserver<Empty> responseObserver
    ) {

        try {

            validateSensorEvent(request);

            log.info(
                    "Received sensor event: hubId={}, sensorId={}, type={}",
                    request.getHubId(),
                    request.getId(),
                    request.getPayloadCase()
            );

            SensorEventHandler handler =
                    eventFactory.getSensorEventHandler(
                            request.getPayloadCase()
                    );

            if (handler == null) {
                throw new IllegalArgumentException(
                        "Unsupported sensor event type: "
                                + request.getPayloadCase()
                );
            }

            handler.handle(request);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {

            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );

        } catch (Exception e) {

            log.error("Failed to process sensor event", e);

            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Internal server error")
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void collectHubEvent(
            HubEventProto request,
            StreamObserver<Empty> responseObserver
    ) {

        try {

            validateHubEvent(request);

            log.info(
                    "Received hub event: hubId={}, type={}",
                    request.getHubId(),
                    request.getPayloadCase()
            );

            HubEventHandler handler =
                    eventFactory.getHubEventHandler(
                            request.getPayloadCase()
                    );

            if (handler == null) {
                throw new IllegalArgumentException(
                        "Unsupported hub event type: "
                                + request.getPayloadCase()
                );
            }

            handler.handle(request);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {

            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );

        } catch (Exception e) {

            log.error("Failed to process hub event", e);

            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Internal server error")
                            .asRuntimeException()
            );
        }
    }

    private void validateSensorEvent(SensorEventProto event) {

        if (event.getHubId().isBlank()) {
            throw new IllegalArgumentException("hubId is empty");
        }

        if (event.getId().isBlank()) {
            throw new IllegalArgumentException("sensor id is empty");
        }

        if (event.getPayloadCase()
                == SensorEventProto.PayloadCase.PAYLOAD_NOT_SET) {

            throw new IllegalArgumentException(
                    "Sensor payload is not set"
            );
        }
    }

    private void validateHubEvent(HubEventProto event) {

        if (event.getHubId().isBlank()) {
            throw new IllegalArgumentException("hubId is empty");
        }

        if (event.getPayloadCase()
                == HubEventProto.PayloadCase.PAYLOAD_NOT_SET) {

            throw new IllegalArgumentException(
                    "Hub payload is not set"
            );
        }
    }
}