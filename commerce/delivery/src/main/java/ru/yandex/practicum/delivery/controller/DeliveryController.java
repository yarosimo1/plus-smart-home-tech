package ru.yandex.practicum.delivery.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.delivery.service.DeliveryService;
import ru.yandex.practicum.interactionapi.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interactionapi.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PutMapping
    public DeliveryDto planDelivery(@RequestBody @Valid DeliveryDto deliveryDto) { return deliveryService.planDelivery(deliveryDto); }

    @PostMapping("/successful")
    public void deliverySuccessful(@RequestBody UUID orderId) { deliveryService.deliverySuccessful(orderId); }

    @PostMapping("/picked")
    public void deliveryPicked(@RequestBody UUID orderId) { deliveryService.deliveryPicked(orderId); }

    @PostMapping("/failed")
    public void deliveryFailed(@RequestBody UUID orderId) { deliveryService.deliveryFailed(orderId); }

    @PostMapping("/cost")
    public BigDecimal deliveryCost(@RequestBody @Valid OrderDto orderDto) { return deliveryService.deliveryCost(orderDto); }
}
