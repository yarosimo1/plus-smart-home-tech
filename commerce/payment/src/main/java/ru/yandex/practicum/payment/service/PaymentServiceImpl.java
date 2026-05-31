package ru.yandex.practicum.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interactionapi.dto.order.OrderDto;
import ru.yandex.practicum.interactionapi.dto.payment.PaymentState;
import ru.yandex.practicum.interactionapi.dto.payment.PaymentDto;
import ru.yandex.practicum.interactionapi.exception.NoOrderFoundException;
import ru.yandex.practicum.interactionapi.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.payment.client.OrderClient;
import ru.yandex.practicum.payment.client.ShoppingStoreClient;
import ru.yandex.practicum.payment.mapper.PaymentMapper;
import ru.yandex.practicum.payment.model.Payment;
import ru.yandex.practicum.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    @Value("${payment.vat-rate:0.10}")
    private BigDecimal vatRate;

    @Override
    @Transactional
    public PaymentDto payment(OrderDto order) {
        validateOrder(order);
        BigDecimal productTotal = productCost(order);
        BigDecimal deliveryTotal = require(order.getDeliveryPrice(), "deliveryPrice is required");
        BigDecimal feeTotal = productTotal.multiply(vatRate);
        Payment payment = Payment.builder()
                .orderId(order.getOrderId())
                .productTotal(productTotal)
                .deliveryTotal(deliveryTotal)
                .feeTotal(feeTotal)
                .totalPayment(productTotal.add(feeTotal).add(deliveryTotal))
                .state(PaymentState.PENDING)
                .build();
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    public BigDecimal getTotalCost(OrderDto order) {
        BigDecimal productTotal = order.getProductPrice() != null ? order.getProductPrice() : productCost(order);
        BigDecimal deliveryTotal = require(order.getDeliveryPrice(), "deliveryPrice is required");
        return productTotal.add(productTotal.multiply(vatRate)).add(deliveryTotal);
    }

    @Override
    @Transactional
    public void refund(UUID paymentId) {
        Payment payment = getPayment(paymentId);
        payment.setState(PaymentState.SUCCESS);
        orderClient.paymentSuccess(payment.getOrderId());
    }

    @Override
    public BigDecimal productCost(OrderDto order) {
        validateOrder(order);
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<UUID, Long> entry : order.getProducts().entrySet()) {
            BigDecimal price = shoppingStoreClient.getProduct(entry.getKey()).price();
            total = total.add(price.multiply(BigDecimal.valueOf(entry.getValue())));
        }
        return total;
    }

    @Override
    @Transactional
    public void paymentFailed(UUID paymentId) {
        Payment payment = getPayment(paymentId);
        payment.setState(PaymentState.FAILED);
        orderClient.paymentFailed(payment.getOrderId());
    }

    private Payment getPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(() -> new NoOrderFoundException(paymentId));
    }

    private void validateOrder(OrderDto order) {
        if (order == null || order.getOrderId() == null || order.getProducts() == null || order.getProducts().isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException("Order id and products are required");
        }
    }

    private BigDecimal require(BigDecimal value, String message) {
        if (value == null) throw new NotEnoughInfoInOrderToCalculateException(message);
        return value;
    }
}
