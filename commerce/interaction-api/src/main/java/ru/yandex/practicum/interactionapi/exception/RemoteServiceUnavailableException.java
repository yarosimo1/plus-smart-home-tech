package ru.yandex.practicum.interactionapi.exception;

import org.springframework.http.HttpStatus;

public class RemoteServiceUnavailableException extends ApiException {
    public RemoteServiceUnavailableException(String serviceName) {
        super(
                "Service unavailable: " + serviceName,
                "Сервис " + serviceName + " временно недоступен",
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }
}
