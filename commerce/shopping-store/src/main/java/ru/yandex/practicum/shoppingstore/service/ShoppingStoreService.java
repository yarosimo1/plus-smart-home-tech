package ru.yandex.practicum.shoppingstore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.interactionapi.dto.store.ProductCategory;
import ru.yandex.practicum.interactionapi.dto.store.ProductDto;
import ru.yandex.practicum.interactionapi.dto.store.ProductState;
import ru.yandex.practicum.interactionapi.dto.store.QuantityState;
import ru.yandex.practicum.interactionapi.exception.ProductNotFoundException;
import ru.yandex.practicum.shoppingstore.mapper.ProductMapper;
import ru.yandex.practicum.shoppingstore.model.Product;
import ru.yandex.practicum.shoppingstore.repository.ProductRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingStoreService {
    private final ProductRepository repository;
    private final ProductMapper mapper;

    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        return repository.findAllByProductCategoryAndProductState(category, ProductState.ACTIVE, pageable).map(mapper::toDto);
    }

    public ProductDto createProduct(ProductDto dto) {
        Product product = mapper.toEntity(dto);
        return mapper.toDto(repository.save(product));
    }

    public ProductDto updateProduct(ProductDto dto) {
        Product product = repository.findById(dto.productId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + dto.productId()));
        product.setProductName(dto.productName());
        product.setDescription(dto.description());
        product.setImageSrc(dto.imageSrc());
        product.setQuantityState(dto.quantityState());
        product.setProductState(dto.productState());
        product.setProductCategory(dto.productCategory());
        product.setPrice(dto.price());
        return mapper.toDto(repository.save(product));
    }

    public boolean removeProductFromStore(UUID productId) {
        Product product = repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
        product.setProductState(ProductState.DEACTIVATE);
        repository.save(product);
        return true;
    }

    public boolean setProductQuantityState(String rawProductId, String rawQuantityState) {
        UUID productId = parseUuid(rawProductId);
        QuantityState quantityState = parseQuantityState(rawQuantityState);
        Product product = repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
        product.setQuantityState(quantityState);
        repository.save(product);
        return true;
    }

    public ProductDto getProduct(UUID productId) {
        return mapper.toDto(repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId)));
    }

    private UUID parseUuid(String raw) {
        try {
            return UUID.fromString(raw);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid productId");
        }
    }

    private QuantityState parseQuantityState(String raw) {
        try {
            return QuantityState.valueOf(raw.trim().toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid quantityState");
        }
    }
}
