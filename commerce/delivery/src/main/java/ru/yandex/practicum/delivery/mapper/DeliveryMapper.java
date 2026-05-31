package ru.yandex.practicum.delivery.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.delivery.model.Delivery;
import ru.yandex.practicum.delivery.model.DeliveryAddress;
import ru.yandex.practicum.interactionapi.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interactionapi.dto.warehouse.AddressDto;

@Component
public class DeliveryMapper {
    public Delivery toEntity(DeliveryDto dto) {
        return Delivery.builder()
                .deliveryId(dto.getDeliveryId())
                .fromAddress(toEntityAddress(dto.getFromAddress()))
                .toAddress(toEntityAddress(dto.getToAddress()))
                .orderId(dto.getOrderId())
                .deliveryState(dto.getDeliveryState())
                .build();
    }

    public DeliveryDto toDto(Delivery delivery) {
        return DeliveryDto.builder()
                .deliveryId(delivery.getDeliveryId())
                .fromAddress(toDtoAddress(delivery.getFromAddress()))
                .toAddress(toDtoAddress(delivery.getToAddress()))
                .orderId(delivery.getOrderId())
                .deliveryState(delivery.getDeliveryState())
                .build();
    }

    private DeliveryAddress toEntityAddress(AddressDto dto) {
        return DeliveryAddress.builder()
                .country(dto.getCountry())
                .city(dto.getCity())
                .street(dto.getStreet())
                .house(dto.getHouse())
                .flat(dto.getFlat())
                .build();
    }

    private AddressDto toDtoAddress(DeliveryAddress address) {
        return AddressDto.builder().
                country(address.getCountry())
                .city(address.getCity())
                .street(address.getStreet())
                .house(address.getHouse())
                .flat(address.getFlat())
                .build();
    }
}
