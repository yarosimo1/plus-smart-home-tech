package ru.yandex.practicum.delivery.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.interactionapi.dto.order.OrderDto;
import ru.yandex.practicum.interactionapi.exception.RemoteServiceUnavailableException;

import java.util.UUID;

@Slf4j
@Component
public class OrderClientFallback implements OrderClient {
    private static final String SERVICE_NAME = "order";

    @Override
    public OrderDto delivery(UUID orderId) {
        throwUnavailable();
        return null;
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        throwUnavailable();
        return null;
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        throwUnavailable();
        return null;
    }

    private void throwUnavailable() {
        log.error("{} service unavailable", SERVICE_NAME);
        throw new RemoteServiceUnavailableException(SERVICE_NAME);
    }
}
