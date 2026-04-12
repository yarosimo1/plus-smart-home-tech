package ru.practicum.collector.sensor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.collector.sensor.event.SensorEvent;
import ru.practicum.collector.sensor.service.SensorService;

@RestController
@RequestMapping("/events")
@Validated
@RequiredArgsConstructor
public class SensorController {
    private final SensorService sensorService;

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        sensorService.send(event);
    }
}