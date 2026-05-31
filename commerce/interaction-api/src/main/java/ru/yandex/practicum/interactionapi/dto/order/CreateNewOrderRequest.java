package ru.yandex.practicum.interactionapi.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.interactionapi.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interactionapi.dto.warehouse.AddressDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateNewOrderRequest {
    @NotNull
    @Valid
    private ShoppingCartDto shoppingCart;
    @NotNull
    @Valid
    private AddressDto deliveryAddress;
}
