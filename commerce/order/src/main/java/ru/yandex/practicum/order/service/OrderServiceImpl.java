package ru.yandex.practicum.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.interactionapi.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interactionapi.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interactionapi.dto.delivery.DeliveryState;
import ru.yandex.practicum.interactionapi.dto.order.*;
import ru.yandex.practicum.interactionapi.dto.payment.PaymentDto;
import ru.yandex.practicum.interactionapi.dto.warehouse.*;
import ru.yandex.practicum.interactionapi.exception.NoOrderFoundException;
import ru.yandex.practicum.interactionapi.exception.NotAuthorizedUserException;
import ru.yandex.practicum.order.client.DeliveryClient;
import ru.yandex.practicum.order.client.PaymentClient;
import ru.yandex.practicum.order.client.WarehouseClient;
import ru.yandex.practicum.order.mapper.OrderMapper;
import ru.yandex.practicum.order.model.Order;
import ru.yandex.practicum.order.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private static final String DEFAULT_USERNAME = "anonymous";

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WarehouseClient warehouseClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;

    @Override
    public List<OrderDto> getClientOrders(String username) {
        if (!StringUtils.hasText(username)) {
            throw new NotAuthorizedUserException(username);
        }
        return orderRepository.findAllByUsername(username).stream().map(orderMapper::toDto).toList();
    }

    @Override
    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        ShoppingCartDto cart = request.getShoppingCart();
        Order order = Order.builder()
                .username(DEFAULT_USERNAME)
                .shoppingCartId(cart.getShoppingCartId())
                .products(cart.getProducts())
                .state(OrderState.NEW)
                .deliveryAddress(orderMapper.toEntityAddress(request.getDeliveryAddress()))
                .build();
        order = orderRepository.save(order);

        BookedProductsDto booked = warehouseClient.assemblyProductForOrderFromShoppingCart(
                AssemblyProductsForOrderRequest.builder()
                        .orderId(order.getOrderId())
                        .products(cart.getProducts())
                        .build()
        );
        order.setDeliveryWeight(booked.getDeliveryWeight());
        order.setDeliveryVolume(booked.getDeliveryVolume());
        order.setFragile(booked.isFragile());
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto productReturn(ProductReturnRequest request) {
        Order order = getOrder(request.getOrderId());
        warehouseClient.returnProducts(AssemblyProductsForOrderRequest.builder()
                .orderId(order.getOrderId())
                .products(request.getProducts())
                .build());
        order.setState(OrderState.PRODUCT_RETURNED);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto payment(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.PAID);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto delivery(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.DELIVERED);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto complete(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.COMPLETED);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = getOrder(orderId);
        BigDecimal total = paymentClient.getTotalCost(orderMapper.toDto(order));
        order.setTotalPrice(total);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = getOrder(orderId);
        if (order.getDeliveryId() == null) {
            AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();
            DeliveryDto planned = deliveryClient.planDelivery(DeliveryDto.builder()
                    .fromAddress(warehouseAddress)
                    .toAddress(orderMapper.toDtoAddress(order.getDeliveryAddress()))
                    .orderId(order.getOrderId())
                    .deliveryState(DeliveryState.CREATED)
                    .build());
            order.setDeliveryId(planned.getDeliveryId());
        }
        BigDecimal deliveryCost = deliveryClient.deliveryCost(orderMapper.toDto(order));
        order.setDeliveryPrice(deliveryCost);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto assembly(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.ASSEMBLED);
        if (order.getProductPrice() == null) {
            order.setProductPrice(paymentClient.productCost(orderMapper.toDto(order)));
        }
        PaymentDto payment = paymentClient.payment(orderMapper.toDto(order));
        order.setPaymentId(payment.getPaymentId());
        order.setState(OrderState.ON_PAYMENT);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        return orderMapper.toDto(order);
    }

    private Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new NoOrderFoundException(orderId));
    }
}
