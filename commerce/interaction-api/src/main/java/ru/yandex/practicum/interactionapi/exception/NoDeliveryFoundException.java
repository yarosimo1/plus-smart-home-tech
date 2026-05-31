package ru.yandex.practicum.interactionapi.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class NoDeliveryFoundException extends ApiException {
    public NoDeliveryFoundException(UUID deliveryId) {
        super("Delivery not found: " + deliveryId, "Доставка не найдена", HttpStatus.NOT_FOUND);
    }
}
