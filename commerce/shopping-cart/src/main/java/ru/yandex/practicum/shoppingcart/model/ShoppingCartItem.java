package ru.yandex.practicum.shoppingcart.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "shopping_cart_items")
@Getter
@Setter
public class ShoppingCartItem {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private ShoppingCart cart;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "quantity", nullable = false)
    private Long quantity;
}
