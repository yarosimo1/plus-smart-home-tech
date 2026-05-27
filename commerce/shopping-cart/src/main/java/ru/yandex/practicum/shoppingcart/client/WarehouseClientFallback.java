package ru.yandex.practicum.shoppingcart.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.interactionapi.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interactionapi.dto.warehouse.AddressDto;
import ru.yandex.practicum.interactionapi.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interactionapi.exception.WarehouseUnavailableException;

@Slf4j
@Component
public class WarehouseClientFallback implements WarehouseClient {

    @Override
    public BookedProductsDto checkProducts(ShoppingCartDto cart) {

        log.error("Warehouse service unavailable");

        throw new WarehouseUnavailableException(
                "Сервис склада недоступен",
                "Попробуйте повторить позже",
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    @Override
    public AddressDto getWarehouseAddress() {

        log.error("Warehouse service unavailable");

        throw new WarehouseUnavailableException(
                "Сервис склада недоступен",
                "Адрес склада временно недоступен",
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }
}