package ru.yandex.practicum.shoppingcart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interactionapi.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interactionapi.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.shoppingcart.client.ShoppingStoreClient;
import ru.yandex.practicum.shoppingcart.client.WarehouseClient;
import ru.yandex.practicum.shoppingcart.mapper.ShoppingCartMapper;
import ru.yandex.practicum.shoppingcart.model.ShoppingCart;
import ru.yandex.practicum.shoppingcart.model.ShoppingCartItem;
import ru.yandex.practicum.shoppingcart.repository.ShoppingCartRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final ShoppingStoreClient shoppingStoreClient;
    private final WarehouseClient warehouseClient;

    @Transactional
    public ShoppingCartDto getShoppingCart(String username) {
        ShoppingCart cart = getOrCreateCart(username);
        return shoppingCartMapper.toDto(cart);
    }

    @Transactional
    public ShoppingCartDto addProducts(String username, Map<UUID, Long> products) {
        ShoppingCart cart = getOrCreateCart(username);
        assertCartIsActive(cart);

        if (products == null || products.isEmpty()) {
            return shoppingCartMapper.toDto(cart);
        }

        products.forEach((productId, quantity) -> {
            ShoppingCartItem item = findItem(cart, productId);
            if (item == null) {
                ShoppingCartItem newItem = new ShoppingCartItem();
                newItem.setCart(cart);
                newItem.setProductId(productId);
                newItem.setQuantity(quantity);
                cart.getItems().add(newItem);
            } else {
                item.setQuantity(item.getQuantity() + quantity);
            }
        });

        return shoppingCartMapper.toDto(shoppingCartRepository.save(cart));
    }

    @Transactional
    public ShoppingCartDto removeProducts(String username, List<UUID> productIds) {
        ShoppingCart cart = getOrCreateCart(username);
        if (productIds == null || productIds.isEmpty()) {
            return shoppingCartMapper.toDto(cart);
        }
        cart.getItems().removeIf(item -> productIds.contains(item.getProductId()));
        return shoppingCartMapper.toDto(shoppingCartRepository.save(cart));
    }

    @Transactional
    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        ShoppingCart cart = getOrCreateCart(username);
        assertCartIsActive(cart);
        if (request == null || request.getProductId() == null || request.getNewQuantity() == null) {
            return shoppingCartMapper.toDto(cart);
        }
        ShoppingCartItem item = findItem(cart, request.getProductId());
        if (item != null) {
            item.setQuantity(request.getNewQuantity());
        }
        return shoppingCartMapper.toDto(shoppingCartRepository.save(cart));
    }

    @Transactional
    public void deactivate(String username) {
        ShoppingCart cart = getOrCreateCart(username);

        cart.setActive(false);

        shoppingCartRepository.save(cart);
    }

    private ShoppingCart getOrCreateCart(String username) {
        return shoppingCartRepository
                .findByUsernameAndActiveTrue(username)
                .orElseGet(() -> {

                    ShoppingCart cart = new ShoppingCart();

                    cart.setUsername(username);
                    cart.setActive(true);

                    return shoppingCartRepository.save(cart);
                });
    }

    private ShoppingCartItem findItem(ShoppingCart cart, UUID productId) {
        return cart.getItems()
                .stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    private void checkProductsAvailability(ShoppingCart cart) {
        ShoppingCartDto dto = shoppingCartMapper.toDto(cart);

        warehouseClient.checkProducts(dto);
    }

    private void assertCartIsActive(ShoppingCart cart) {
        if (!cart.isActive()) {
            throw new IllegalStateException(
                    "Shopping cart is deactivated"
            );
        }
    }
}