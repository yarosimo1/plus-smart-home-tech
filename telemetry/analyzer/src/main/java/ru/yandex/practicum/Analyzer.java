package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.processors.HubEventProcessor;
import ru.yandex.practicum.processors.SnapshotProcessor;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Analyzer {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(Analyzer.class, args);

        HubEventProcessor hubEventProcessor =
                context.getBean(HubEventProcessor.class);

        SnapshotProcessor snapshotProcessor =
                context.getBean(SnapshotProcessor.class);

        // запускаем второй поток
        Thread hubThread = new Thread(hubEventProcessor);
        hubThread.setName("hub-events-thread");
        hubThread.start();

        // основной поток
        snapshotProcessor.start();
    }
}