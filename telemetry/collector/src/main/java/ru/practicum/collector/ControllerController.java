package ru.practicum.collector;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
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

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class ControllerController extends CollectorControllerGrpc.CollectorControllerImplBase {
    private final EventFactory eventFactory;

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Получения контроллером собщения {}", request);
            SensorEventHandler sensorEventProto =
                    eventFactory.getSensorEvetnHandler(request.getPayloadCase());

            if (sensorEventProto == null) {
                throw new IllegalArgumentException("Unknown event type: " + request.getPayloadCase());
            }

            sensorEventProto.handle(request);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Получения контроллером собщения {}", request);
            HubEventHandler hubEventHandler =
                    eventFactory.getHubEventHandler(request.getPayloadCase());

            if (hubEventHandler == null) {
                throw new IllegalArgumentException("Unknown event type: " + request.getPayloadCase());
            }

            hubEventHandler.handle(request);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}