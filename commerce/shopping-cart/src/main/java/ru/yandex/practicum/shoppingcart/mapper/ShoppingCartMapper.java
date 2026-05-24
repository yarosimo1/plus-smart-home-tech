package ru.yandex.practicum.shoppingcart.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interactionapi.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.shoppingcart.model.ShoppingCart;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Component
public class ShoppingCartMapper {

    public ShoppingCartDto toDto(ShoppingCart cart) {
        return ShoppingCartDto.builder()
                .shoppingCartId(cart.getId())
                .products(cart.getItems().stream()
                        .collect(Collectors.toMap(
                                item -> item.getProductId(),
                                item -> item.getQuantity(),
                                Long::sum,
                                LinkedHashMap::new
                        )))
                .build();
    }
}
