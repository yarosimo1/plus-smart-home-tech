package ru.yandex.practicum.shoppingcart.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interactionapi.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interactionapi.dto.warehouse.AddressDto;
import ru.yandex.practicum.interactionapi.dto.warehouse.BookedProductsDto;

@FeignClient(
        name = "warehouse",
        path = "/api/v1/warehouse",
        fallback = WarehouseClientFallback.class
)
public interface WarehouseClient {

    @PostMapping("/check")
    BookedProductsDto checkProducts(
            @RequestBody ShoppingCartDto cart
    );

    @GetMapping("/address")
    AddressDto getWarehouseAddress();
}
