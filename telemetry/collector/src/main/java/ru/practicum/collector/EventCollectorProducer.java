package ru.practicum.collector;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventCollectorProducer {
    private final KafkaProducer<String, Object> producer;
    private final TopicRouter router;

    public void send(String key, Object event) {
        String topic = router.resolveTopic(event);

        ProducerRecord<String, Object> record =
                new ProducerRecord<>(topic, key, event);

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                System.err.println("Ошибка отправки: " + exception.getMessage());
            } else {
                System.out.println(
                        "Отправлено: topic=" + metadata.topic() +
                                ", partition=" + metadata.partition() +
                                ", offset=" + metadata.offset()
                );
            }
        });
    }
}