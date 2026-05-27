package ru.yandex.practicum.shoppingcart.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.interactionapi.dto.store.ProductDto;
import ru.yandex.practicum.interactionapi.exception.ProductServiceUnavailableException;

import java.util.UUID;

@Slf4j
@Component
public class ShoppingStoreClientFallback implements ShoppingStoreClient {

    @Override
    public ProductDto getProduct(UUID productId) {

        log.error("Shopping-store service unavailable");

        throw new ProductServiceUnavailableException(
                "Сервис товаров недоступен",
                "Попробуйте повторить позже",
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }
}