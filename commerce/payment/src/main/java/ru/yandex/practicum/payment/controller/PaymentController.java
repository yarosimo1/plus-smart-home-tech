package ru.yandex.practicum.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interactionapi.dto.order.OrderDto;
import ru.yandex.practicum.interactionapi.dto.payment.PaymentDto;
import ru.yandex.practicum.payment.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public PaymentDto payment(@RequestBody @Valid OrderDto order) {
        return paymentService.payment(order);
    }

    @PostMapping("/totalCost")
    public BigDecimal getTotalCost(@RequestBody @Valid OrderDto order) {
        return paymentService.getTotalCost(order);
    }

    @PostMapping("/refund")
    public void refund(@RequestBody UUID paymentId) {
        paymentService.refund(paymentId);
    }

    @PostMapping("/productCost")
    public BigDecimal productCost(@RequestBody @Valid OrderDto order) {
        return paymentService.productCost(order);
    }

    @PostMapping("/failed")
    public void paymentFailed(@RequestBody UUID paymentId) {
        paymentService.paymentFailed(paymentId);
    }
}
