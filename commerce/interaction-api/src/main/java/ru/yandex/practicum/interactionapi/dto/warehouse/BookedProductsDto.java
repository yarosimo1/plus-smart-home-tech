package ru.yandex.practicum.interactionapi.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookedProductsDto {

    @NotNull
    @PositiveOrZero
    private Double deliveryWeight;

    @NotNull
    @PositiveOrZero
    private Double deliveryVolume;

    private boolean fragile;
}
