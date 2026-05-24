package ru.yandex.practicum.warehouse.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interactionapi.dto.warehouse.AddressDto;
import ru.yandex.practicum.interactionapi.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interactionapi.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.model.WarehouseProduct;

@Component
public class WarehouseMapper {

    public WarehouseProduct toEntity(NewProductInWarehouseRequest request) {
        return WarehouseProduct.builder()
                .productId(request.getProductId())
                .quantity(0L)
                .fragile(request.isFragile())
                .weight(request.getWeight())
                .width(request.getDimension().getWidth())
                .height(request.getDimension().getHeight())
                .depth(request.getDimension().getDepth())
                .build();
    }

    public AddressDto toAddressDto(String address) {
        return AddressDto.builder()
                .country(address)
                .city(address)
                .street(address)
                .house(address)
                .flat(address)
                .build();
    }

    public BookedProductsDto toBookedProductsDto(double totalWeight, double totalVolume, boolean fragile) {
        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(fragile)
                .build();
    }
}
