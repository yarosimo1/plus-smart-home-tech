package ru.yandex.practicum.warehouse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interactionapi.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interactionapi.dto.warehouse.*;
import ru.yandex.practicum.interactionapi.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.interactionapi.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.interactionapi.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.warehouse.model.OrderBooking;
import ru.yandex.practicum.warehouse.model.WarehouseProduct;
import ru.yandex.practicum.warehouse.repository.OrderBookingRepository;
import ru.yandex.practicum.warehouse.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {
    private static final String[] ADDRESSES = {
            "ADDRESS_1",
            "ADDRESS_2"
    };
    private static final Random RANDOM = Random.from(new SecureRandom());
    private final WarehouseRepository repository;
    private final OrderBookingRepository orderBookingRepository;

    @Override
    @Transactional
    public void createProduct(NewProductInWarehouseRequest request) {

        if (repository.existsById(request.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException(
                    "Товар уже зарегистрирован на складе"
            );
        }

        WarehouseProduct product = WarehouseProduct.builder()
                .productId(request.getProductId())
                .quantity(0L)
                .fragile(request.isFragile())
                .weight(request.getWeight())
                .width(request.getDimension().getWidth())
                .height(request.getDimension().getHeight())
                .depth(request.getDimension().getDepth())
                .build();

        repository.save(product);
    }

    @Override
    @Transactional
    public void addQuantity(AddProductToWarehouseRequest request) {
        WarehouseProduct product = repository.findById(
                        request.getProductId()
                )
                .orElseThrow(() ->
                        new NoSpecifiedProductInWarehouseException(
                                "Товар отсутствует на складе"
                        )
                );

        product.setQuantity(
                product.getQuantity() + request.getQuantity()
        );
    }

    @Override
    public BookedProductsDto checkProducts(
            ShoppingCartDto shoppingCartDto) throws ProductInShoppingCartLowQuantityInWarehouse {
        double totalWeight = 0;
        double totalVolume = 0;

        boolean fragile = false;

        for (Map.Entry<UUID, Long> entry : shoppingCartDto.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Long requestedQuantity = entry.getValue();

            WarehouseProduct product = repository.findById(productId)
                    .orElseThrow(() ->
                            new NoSpecifiedProductInWarehouseException(
                                    "Товар отсутствует на складе: "
                                            + productId
                            )
                    );

            if (product.getQuantity() < requestedQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        "Недостаточно товара на складе: "
                                + productId
                );
            }

            totalWeight += product.getWeight() * requestedQuantity;

            double volume = product.getWidth()
                    * product.getHeight()
                    * product.getDepth();

            totalVolume += volume * requestedQuantity;

            if (product.isFragile()) {
                fragile = true;
            }
        }

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(fragile)
                .build();
    }

    @Override
    public AddressDto getAddress() {

        String currentAddress =
                ADDRESSES[RANDOM.nextInt(ADDRESSES.length)];

        return AddressDto.builder()
                .country("Россия")
                .city("Москва")
                .street(currentAddress)
                .house("10")
                .flat("5")
                .build();
    }

    @Override
    @Transactional
    public BookedProductsDto assemblyProductForOrderFromShoppingCart(AssemblyProductsForOrderRequest request) {
        double totalWeight = 0;
        double totalVolume = 0;
        boolean fragile = false;

        for (Map.Entry<UUID, Long> entry : request.getProducts().entrySet()) {
            WarehouseProduct product = repository.findById(entry.getKey())
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Товар отсутствует на складе: " + entry.getKey()));
            if (product.getQuantity() < entry.getValue()) {
                throw new ProductInShoppingCartLowQuantityInWarehouse("Недостаточно товара на складе: " + entry.getKey());
            }
        }

        for (Map.Entry<UUID, Long> entry : request.getProducts().entrySet()) {
            WarehouseProduct product = repository.findById(entry.getKey()).orElseThrow();
            product.setQuantity(product.getQuantity() - entry.getValue());
            totalWeight += product.getWeight() * entry.getValue();
            totalVolume += product.getWidth() * product.getHeight() * product.getDepth() * entry.getValue();
            fragile = fragile || product.isFragile();
        }

        orderBookingRepository.save(OrderBooking.builder()
                .orderId(request.getOrderId())
                .products(request.getProducts())
                .build());

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(fragile)
                .build();
    }

    @Override
    @Transactional
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        OrderBooking booking = orderBookingRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Бронирование заказа отсутствует: " + request.getOrderId()));
        booking.setDeliveryId(request.getDeliveryId());
    }

    @Override
    @Transactional
    public void returnProducts(AssemblyProductsForOrderRequest request) {
        for (Map.Entry<UUID, Long> entry : request.getProducts().entrySet()) {
            WarehouseProduct product = repository.findById(entry.getKey())
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Товар отсутствует на складе: " + entry.getKey()));
            product.setQuantity(product.getQuantity() + entry.getValue());
        }
    }

}
