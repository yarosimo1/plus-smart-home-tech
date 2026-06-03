package ru.yandex.practicum.payment.client;

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
    public OrderDto paymentSuccess(UUID orderId) {
        return throwUnavailable();
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        return throwUnavailable();
    }

    private <T> T throwUnavailable() {
        log.error("{} service unavailable", SERVICE_NAME);
        throw new RemoteServiceUnavailableException(SERVICE_NAME);
    }
}
