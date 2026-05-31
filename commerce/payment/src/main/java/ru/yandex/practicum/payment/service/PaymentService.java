package ru.yandex.practicum.payment.service;

import ru.yandex.practicum.interactionapi.dto.order.OrderDto;
import ru.yandex.practicum.interactionapi.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    PaymentDto payment(OrderDto order);
    BigDecimal getTotalCost(OrderDto order);
    void refund(UUID paymentId);
    BigDecimal productCost(OrderDto order);
    void paymentFailed(UUID paymentId);
}
