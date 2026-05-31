package ru.yandex.practicum.warehouse.service;

import ru.yandex.practicum.interactionapi.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interactionapi.dto.warehouse.*;

public interface WarehouseService {

    void createProduct(NewProductInWarehouseRequest request);

    void addQuantity(AddProductToWarehouseRequest request);

    BookedProductsDto checkProducts(ShoppingCartDto cart);

    AddressDto getAddress();

    BookedProductsDto assemblyProductForOrderFromShoppingCart(AssemblyProductsForOrderRequest request);

    void shippedToDelivery(ShippedToDeliveryRequest request);

    void returnProducts(AssemblyProductsForOrderRequest request);
}
