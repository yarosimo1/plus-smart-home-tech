package ru.yandex.practicum.delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interactionapi.dto.warehouse.ShippedToDeliveryRequest;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse", fallback = WarehouseClientFallback.class)
public interface WarehouseClient {
    @PostMapping("/shipped")
    void shippedToDelivery(@RequestBody ShippedToDeliveryRequest request);
}