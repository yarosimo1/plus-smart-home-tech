package ru.yandex.practicum.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interactionapi.dto.warehouse.*;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse", fallback = WarehouseClientFallback.class)
public interface WarehouseClient {
    @PostMapping("/assembly")
    BookedProductsDto assemblyProductForOrderFromShoppingCart(@RequestBody AssemblyProductsForOrderRequest request);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();

    @PostMapping("/return")
    void returnProducts(@RequestBody AssemblyProductsForOrderRequest request);
}
