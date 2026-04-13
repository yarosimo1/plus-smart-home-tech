package ru.practicum.collector;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.io.IOException;

public class CollectorAvroDeserializer implements Deserializer<SpecificRecordBase> {
    private final DecoderFactory decoderFactory = DecoderFactory.get();

    @Override
    public SpecificRecordBase deserialize(String s, byte[] bytes) {
        try {
            if (bytes != null) {
                BinaryDecoder decoder = decoderFactory.binaryDecoder(bytes, null);
                DatumReader<SpecificRecordBase> reader;

                switch (s) {
                    case CollectorTopics.TELEMETRY_SENSOR_V1 ->
                            reader = new SpecificDatumReader<>(SensorEventAvro.getClassSchema());
                    case CollectorTopics.TELEMETRY_HUBS_V1 ->
                            reader = new SpecificDatumReader<>(HubEventAvro.getClassSchema());
                    default -> throw new IllegalArgumentException("Неизвестный топик: " + s);
                }
                return reader.read(null, decoder);
            }
            return null;
        } catch (IOException e) {
            throw new SerializationException("Ошибка десереализации данных из топика [" + s + "]", e);
        }
    }
}