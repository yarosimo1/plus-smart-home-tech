package ru.yandex.practicum.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.interactionapi.dto.store.ProductDto;

import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store", fallback = ShoppingStoreClientFallback.class)
public interface ShoppingStoreClient {
    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable UUID productId);
}
