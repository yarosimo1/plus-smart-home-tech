package ru.practicum.collector.mapper;

import com.google.protobuf.Timestamp;
import lombok.experimental.UtilityClass;

import java.time.Instant;

@UtilityClass
public class ProtoTimestampMapper {

    public Instant toInstant(Timestamp timestamp) {

        return Instant.ofEpochSecond(
                timestamp.getSeconds(),
                timestamp.getNanos()
        );
    }
}