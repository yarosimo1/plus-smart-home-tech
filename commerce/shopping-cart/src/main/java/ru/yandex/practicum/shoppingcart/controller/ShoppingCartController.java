package ru.yandex.practicum.shoppingcart.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interactionapi.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interactionapi.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.shoppingcart.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam @NotBlank String username) {
        return shoppingCartService.getShoppingCart(username);
    }

    @PutMapping
    public ShoppingCartDto addProductToShoppingCart(@RequestParam @NotBlank String username,
                                                    @RequestBody Map<UUID, Long> products) {
        return shoppingCartService.addProducts(username, products);
    }

    @DeleteMapping
    public void deactivateCurrentShoppingCart(@RequestParam @NotBlank String username) {
        shoppingCartService.deactivate(username);
    }

    @PostMapping("/remove")
    public ShoppingCartDto removeFromShoppingCart(@RequestParam @NotBlank String username,
                                                  @RequestBody List<UUID> products) {
        return shoppingCartService.removeProducts(username, products);
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeProductQuantity(@RequestParam @NotBlank String username,
                                                 @Valid @RequestBody ChangeProductQuantityRequest request) {
        return shoppingCartService.changeQuantity(username, request);
    }
}
