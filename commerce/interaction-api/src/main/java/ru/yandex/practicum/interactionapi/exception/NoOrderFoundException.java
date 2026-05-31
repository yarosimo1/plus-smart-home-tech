package ru.yandex.practicum.interactionapi.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class NoOrderFoundException extends ApiException {
    public NoOrderFoundException(UUID orderId) {
        super("Order not found: " + orderId, "Заказ не найден", HttpStatus.BAD_REQUEST);
    }
}
