package ru.yandex.practicum.shoppingstore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interactionapi.dto.store.PageProductDto;
import ru.yandex.practicum.interactionapi.dto.store.ProductCategory;
import ru.yandex.practicum.interactionapi.dto.store.ProductDto;
import ru.yandex.practicum.interactionapi.dto.store.QuantityState;
import ru.yandex.practicum.shoppingstore.mapper.PageMapper;
import ru.yandex.practicum.shoppingstore.service.ShoppingStoreService;

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
    public void setProductQuantityState(
            @RequestParam String productId,
            @RequestParam String quantityState
    ) {
        service.setProductQuantityState(
                UUID.fromString(productId),
                QuantityState.valueOf(quantityState)
        );
    }

    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable UUID productId) {
        return service.getProduct(productId);
    }
}
