package ru.yandex.practicum.interactionapi.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class NoPaymentFoundException extends ApiException {
    public NoPaymentFoundException(UUID paymentId) {
        super("Payment not found: " + paymentId, "Платёж не найден", HttpStatus.NOT_FOUND);
    }
}
