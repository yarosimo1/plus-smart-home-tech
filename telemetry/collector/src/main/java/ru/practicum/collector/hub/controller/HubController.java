package ru.practicum.collector.hub.controller;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.collector.hub.event.HubEvent;

@RestController
@RequestMapping("/events")
@Validated
public class HubController {
    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent event) {

    }
}