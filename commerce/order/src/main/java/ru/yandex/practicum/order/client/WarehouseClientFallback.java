package ru.yandex.practicum.order.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.interactionapi.dto.warehouse.*;
import ru.yandex.practicum.interactionapi.exception.RemoteServiceUnavailableException;

@Slf4j
@Component
public class WarehouseClientFallback implements WarehouseClient {
    private static final String SERVICE_NAME = "warehouse";

    @Override
    public BookedProductsDto assemblyProductForOrderFromShoppingCart(AssemblyProductsForOrderRequest request) {
        throwUnavailable();
        return null;
    }

    @Override
    public AddressDto getWarehouseAddress() {
        throwUnavailable();
        return null;
    }

    @Override
    public void returnProducts(AssemblyProductsForOrderRequest request) {
        throwUnavailable();
    }

    private void throwUnavailable() {
        log.error("{} service unavailable", SERVICE_NAME);
        throw new RemoteServiceUnavailableException(SERVICE_NAME);
    }
}
