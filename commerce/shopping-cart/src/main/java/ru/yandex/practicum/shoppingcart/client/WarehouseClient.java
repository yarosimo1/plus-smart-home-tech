package ru.yandex.practicum.shoppingcart.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interactionapi.dto.cart.ShoppingCartDto;

@FeignClient(name = "warehouse")
public interface WarehouseClient {

    @GetMapping("/api/v1/warehouse/check")
    Boolean isAvailable(@RequestBody ShoppingCartDto cart);
}
