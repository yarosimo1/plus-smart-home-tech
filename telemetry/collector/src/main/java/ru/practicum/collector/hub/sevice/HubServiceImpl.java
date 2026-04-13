package ru.practicum.collector.hub.sevice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.collector.EventRouter;
import ru.practicum.collector.hub.event.HubEvent;

@Service
@Slf4j
@RequiredArgsConstructor
public class HubServiceImpl implements HubService {
    private final EventRouter eventRouter;

    @Override
    public void send(HubEvent event) {
        log.info("Отпарвка сообщения хаба");
        eventRouter.routeHubEvent(event);
    }
}