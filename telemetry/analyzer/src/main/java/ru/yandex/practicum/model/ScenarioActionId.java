package ru.yandex.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioActionId implements Serializable {
    private Long scenario;
    private String sensor;
    private Long action;
}