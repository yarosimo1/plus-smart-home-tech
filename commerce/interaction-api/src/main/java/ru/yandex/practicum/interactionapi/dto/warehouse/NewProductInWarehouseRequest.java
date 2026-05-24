package ru.yandex.practicum.interactionapi.dto.warehouse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewProductInWarehouseRequest {

    @NotNull
    private UUID productId;

    private boolean fragile;

    @NotNull
    @Valid
    private DimensionDto dimension;

    @NotNull
    @Positive
    private Double weight;
}
