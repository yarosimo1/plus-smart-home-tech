package ru.yandex.practicum.shoppingstore.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.yandex.practicum.interactionapi.dto.store.ProductCategory;
import ru.yandex.practicum.interactionapi.dto.store.ProductState;
import ru.yandex.practicum.interactionapi.dto.store.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    private UUID productId;
    private String productName;
    private String description;
    private String imageSrc;
    @Enumerated(EnumType.STRING)
    private QuantityState quantityState;
    @Enumerated(EnumType.STRING)
    private ProductState productState;
    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;
    private BigDecimal price;
}
