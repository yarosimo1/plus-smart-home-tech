package ru.yandex.practicum.shoppingstore.controller;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interactionapi.dto.store.PageProductDto;
import ru.yandex.practicum.interactionapi.dto.store.ProductCategory;
import ru.yandex.practicum.interactionapi.dto.store.ProductDto;
import ru.yandex.practicum.shoppingstore.mapper.PageMapper;
import ru.yandex.practicum.shoppingstore.service.ShoppingStoreService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController {
    private final ShoppingStoreService service;
    private final PageMapper pageMapper;

    @GetMapping
    public PageProductDto getProducts(@RequestParam ProductCategory category, Pageable pageable) {
        Page<ProductDto> page = service.getProducts(category, pageable);
        return pageMapper.toPageProductDto(page);
    }

    @PutMapping
    public ProductDto createNewProduct(@Valid @RequestBody ProductDto productDto) {
        return service.createProduct(productDto);
    }

    @PostMapping
    public ProductDto updateProduct(@Valid @RequestBody ProductDto productDto) {
        return service.updateProduct(productDto);
    }

    @PostMapping("/removeProductFromStore")
    public boolean removeProductFromStore(@RequestBody UUID productId) {
        return service.removeProductFromStore(productId);
    }

    @PostMapping("/quantityState")
    public boolean setProductQuantityState(@RequestBody(required = false) JsonNode request,
                                           @RequestParam(required = false) Map<String, String> params) {
        String productId = firstNonBlank(
                text(request, "productId", "productID", "id"),
                params.get("productId"),
                params.get("productID"),
                params.get("id")
        );
        String quantityState = firstNonBlank(
                text(request, "quantityState", "quantity", "state"),
                params.get("quantityState"),
                params.get("quantity"),
                params.get("state")
        );
        return service.setProductQuantityState(productId, quantityState);
    }

    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable UUID productId) {
        return service.getProduct(productId);
    }

    private String text(JsonNode node, String... names) {
        if (node == null) {
            return null;
        }
        for (String name : names) {
            JsonNode value = node.get(name);
            if (value != null && !value.isNull()) {
                return value.asText();
            }
        }
        return null;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
