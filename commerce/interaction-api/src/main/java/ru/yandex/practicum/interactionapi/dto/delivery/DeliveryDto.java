package ru.yandex.practicum.interactionapi.dto.delivery;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.interactionapi.dto.warehouse.AddressDto;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryDto {
    private UUID deliveryId;
    @NotNull
    @Valid
    private AddressDto fromAddress;
    @NotNull
    @Valid
    private AddressDto toAddress;
    @NotNull
    private UUID orderId;
    private DeliveryState deliveryState;
}
