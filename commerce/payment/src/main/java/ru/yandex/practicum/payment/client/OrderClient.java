package ru.yandex.practicum.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interactionapi.dto.order.OrderDto;

import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order", fallback = OrderClientFallback.class)
public interface OrderClient {
    @PostMapping("/payment")
    OrderDto paymentSuccess(@RequestBody UUID orderId);

    @PostMapping("/payment/failed")
    OrderDto paymentFailed(@RequestBody UUID orderId);
}
