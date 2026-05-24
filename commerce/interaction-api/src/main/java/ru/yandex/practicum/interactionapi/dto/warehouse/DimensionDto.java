package ru.yandex.practicum.interactionapi.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DimensionDto {

    @NotNull
    @Positive
    private Double width;

    @NotNull
    @Positive
    private Double height;

    @NotNull
    @Positive
    private Double depth;
}
