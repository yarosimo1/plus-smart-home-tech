package ru.yandex.practicum.order.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.interactionapi.dto.order.OrderDto;
import ru.yandex.practicum.interactionapi.dto.payment.PaymentDto;
import ru.yandex.practicum.interactionapi.exception.RemoteServiceUnavailableException;

import java.math.BigDecimal;

@Slf4j
@Component
public class PaymentClientFallback implements PaymentClient {
    private static final String SERVICE_NAME = "payment";

    @Override
    public BigDecimal productCost(OrderDto orderDto) {
        throwUnavailable();
        return null;
    }

    @Override
    public BigDecimal getTotalCost(OrderDto orderDto) {
        throwUnavailable();
        return null;
    }

    @Override
    public PaymentDto payment(OrderDto orderDto) {
        throwUnavailable();
        return null;
    }

    private void throwUnavailable() {
        log.error("{} service unavailable", SERVICE_NAME);
        throw new RemoteServiceUnavailableException(SERVICE_NAME);
    }
}
