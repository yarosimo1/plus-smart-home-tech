package ru.yandex.practicum.order.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.interactionapi.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interactionapi.dto.order.OrderDto;
import ru.yandex.practicum.interactionapi.exception.RemoteServiceUnavailableException;

import java.math.BigDecimal;

@Slf4j
@Component
public class DeliveryClientFallback implements DeliveryClient {
    private static final String SERVICE_NAME = "delivery";

    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        return throwUnavailable();
    }

    @Override
    public BigDecimal deliveryCost(OrderDto orderDto) {
        return throwUnavailable();
    }

    private <T> T throwUnavailable() {
        log.error("{} service unavailable", SERVICE_NAME);
        throw new RemoteServiceUnavailableException(SERVICE_NAME);
    }
}