package ru.yandex.practicum.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.client.OrderClient;
import ru.yandex.practicum.delivery.client.WarehouseClient;
import ru.yandex.practicum.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.delivery.model.Delivery;
import ru.yandex.practicum.delivery.repository.DeliveryRepository;
import ru.yandex.practicum.interactionapi.dto.delivery.*;
import ru.yandex.practicum.interactionapi.dto.order.OrderDto;
import ru.yandex.practicum.interactionapi.dto.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.interactionapi.exception.NoDeliveryFoundException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    @Value("${delivery.base-cost:5.0}") private BigDecimal baseCost;
    @Value("${delivery.address-1-factor:1.0}") private BigDecimal address1Factor;
    @Value("${delivery.address-2-factor:2.0}") private BigDecimal address2Factor;
    @Value("${delivery.fragile-rate:0.2}") private BigDecimal fragileRate;
    @Value("${delivery.weight-rate:0.3}") private BigDecimal weightRate;
    @Value("${delivery.volume-rate:0.2}") private BigDecimal volumeRate;
    @Value("${delivery.different-street-rate:0.2}") private BigDecimal differentStreetRate;

    @Override
    @Transactional
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = deliveryMapper.toEntity(deliveryDto);
        if (delivery.getDeliveryState() == null) delivery.setDeliveryState(DeliveryState.CREATED);
        return deliveryMapper.toDto(deliveryRepository.save(delivery));
    }

    @Override
    @Transactional
    public void deliverySuccessful(UUID orderId) {
        Delivery delivery = getByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        orderClient.delivery(orderId);
    }

    @Override
    @Transactional
    public void deliveryPicked(UUID orderId) {
        Delivery delivery = getByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        warehouseClient.shippedToDelivery(ShippedToDeliveryRequest.builder()
                .orderId(orderId)
                .deliveryId(delivery.getDeliveryId())
                .build());
        orderClient.assembly(orderId);
    }

    @Override
    @Transactional
    public void deliveryFailed(UUID orderId) {
        Delivery delivery = getByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        orderClient.deliveryFailed(orderId);
    }

    @Override
    public BigDecimal deliveryCost(OrderDto orderDto) {
        Delivery delivery = orderDto.getDeliveryId() == null
                ? getByOrderId(orderDto.getOrderId())
                : deliveryRepository.findById(orderDto.getDeliveryId()).orElseThrow(() -> new NoDeliveryFoundException(orderDto.getDeliveryId()));
        BigDecimal result = baseCost.add(baseCost.multiply(warehouseFactor(delivery)));
        if (orderDto.isFragile()) result = result.add(result.multiply(fragileRate));
        result = result.add(BigDecimal.valueOf(value(orderDto.getDeliveryWeight())).multiply(weightRate));
        result = result.add(BigDecimal.valueOf(value(orderDto.getDeliveryVolume())).multiply(volumeRate));
        if (!Objects.equals(delivery.getFromAddress().getStreet(), delivery.getToAddress().getStreet())) {
            result = result.add(result.multiply(differentStreetRate));
        }
        return result;
    }

    private BigDecimal warehouseFactor(Delivery delivery) {
        String street = delivery.getFromAddress().getStreet();
        if (street != null && street.contains("ADDRESS_2")) return address2Factor;
        return address1Factor;
    }

    private Delivery getByOrderId(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId).orElseThrow(() -> new NoDeliveryFoundException(orderId));
    }

    private double value(Double value) { return value == null ? 0.0 : value; }
}
