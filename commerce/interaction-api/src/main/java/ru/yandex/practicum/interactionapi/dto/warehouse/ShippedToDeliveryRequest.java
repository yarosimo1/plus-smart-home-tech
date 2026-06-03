package ru.yandex.practicum.interactionapi.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippedToDeliveryRequest {
    @NotNull
    private UUID orderId;
    @NotNull
    private UUID deliveryId;
}
