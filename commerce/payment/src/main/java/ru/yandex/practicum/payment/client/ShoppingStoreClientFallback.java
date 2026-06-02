package ru.yandex.practicum.payment.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.interactionapi.dto.store.ProductDto;
import ru.yandex.practicum.interactionapi.exception.RemoteServiceUnavailableException;

import java.util.UUID;

@Slf4j
@Component
public class ShoppingStoreClientFallback implements ShoppingStoreClient {
    private static final String SERVICE_NAME = "shopping-store";

    @Override
    public ProductDto getProduct(UUID productId) {
        return throwUnavailable();
    }

    private <T> T throwUnavailable() {
        log.error("{} service unavailable", SERVICE_NAME);
        throw new RemoteServiceUnavailableException(SERVICE_NAME);
    }
}
