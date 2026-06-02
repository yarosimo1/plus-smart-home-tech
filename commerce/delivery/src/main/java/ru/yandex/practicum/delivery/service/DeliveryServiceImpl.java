package ru.yandex.practicum.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        if (delivery.getDeliveryState() == null) {
            delivery.setDeliveryState(DeliveryState.CREATED);
        }
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
        UUID orderId = orderDto.getOrderId();
        log.info(
                "Starting delivery cost calculation for orderId={}, deliveryId={}, fragile={}, deliveryWeight={}, deliveryVolume={}",
                orderId,
                orderDto.getDeliveryId(),
                orderDto.isFragile(),
                orderDto.getDeliveryWeight(),
                orderDto.getDeliveryVolume()
        );

        Delivery delivery = orderDto.getDeliveryId() == null
                ? getByOrderId(orderId)
                : deliveryRepository.findById(orderDto.getDeliveryId())
                  .orElseThrow(() -> new NoDeliveryFoundException(orderDto.getDeliveryId()));

        BigDecimal warehouseFactor = warehouseFactor(delivery);
        BigDecimal result = baseCost.add(baseCost.multiply(warehouseFactor));
        log.info(
                "Delivery cost warehouse factor applied for orderId={}: deliveryId={}, fromStreet={}, toStreet={}, baseCost={}, warehouseFactor={}, subtotal={}",
                orderId,
                delivery.getDeliveryId(),
                delivery.getFromAddress().getStreet(),
                delivery.getToAddress().getStreet(),
                baseCost,
                warehouseFactor,
                result
        );

        if (orderDto.isFragile()) {
            BigDecimal fragileExtra = result.multiply(fragileRate);
            result = result.add(fragileExtra);
            log.info(
                    "Delivery cost fragile factor applied for orderId={}: fragileRate={}, extra={}, subtotal={}",
                    orderId,
                    fragileRate,
                    fragileExtra,
                    result
            );
        }

        double deliveryWeight = value(orderDto.getDeliveryWeight());
        BigDecimal weightExtra = BigDecimal.valueOf(deliveryWeight).multiply(weightRate);
        result = result.add(weightExtra);
        log.info(
                "Delivery cost weight rate applied for orderId={}: deliveryWeight={}, weightRate={}, extra={}, subtotal={}",
                orderId,
                deliveryWeight,
                weightRate,
                weightExtra,
                result
        );

        double deliveryVolume = value(orderDto.getDeliveryVolume());
        BigDecimal volumeExtra = BigDecimal.valueOf(deliveryVolume).multiply(volumeRate);
        result = result.add(volumeExtra);
        log.info(
                "Delivery cost volume rate applied for orderId={}: deliveryVolume={}, volumeRate={}, extra={}, subtotal={}",
                orderId,
                deliveryVolume,
                volumeRate,
                volumeExtra,
                result
        );

        if (!Objects.equals(delivery.getFromAddress().getStreet(), delivery.getToAddress().getStreet())) {
            BigDecimal differentStreetExtra = result.multiply(differentStreetRate);
            result = result.add(differentStreetExtra);
            log.info(
                    "Delivery cost different street rate applied for orderId={}: differentStreetRate={}, extra={}, subtotal={}",
                    orderId,
                    differentStreetRate,
                    differentStreetExtra,
                    result
            );
        }

        log.info("Delivery cost calculated for orderId={}: result={}", orderId, result);

        return result;
    }

    private BigDecimal warehouseFactor(Delivery delivery) {
        String street = delivery.getFromAddress().getStreet();
        if (street != null && street.contains("ADDRESS_2")) {
            return address2Factor;
        }
        return address1Factor;
    }

    private Delivery getByOrderId(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId).orElseThrow(() -> new NoDeliveryFoundException(orderId));
    }

    private double value(Double value) {
        return value == null ? 0.0 : value;
    }
}
