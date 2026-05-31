package ru.yandex.practicum.order.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAddress {
    private String country;
    private String city;
    private String street;
    private String house;
    private String flat;
}
