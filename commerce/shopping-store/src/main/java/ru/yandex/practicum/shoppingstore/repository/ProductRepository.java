package ru.yandex.practicum.shoppingstore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.interactionapi.dto.store.ProductCategory;
import ru.yandex.practicum.interactionapi.dto.store.ProductState;
import ru.yandex.practicum.shoppingstore.model.Product;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Page<Product> findAllByProductCategoryAndProductState(ProductCategory category, ProductState state, Pageable pageable);
}
