package ru.yandex.practicum.warehouse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.util.Random;

@Configuration
public class WarehouseAddressConfig {

    private static final String[] ADDRESSES =
            new String[]{"ADDRESS_1", "ADDRESS_2"};

    @Bean
    public String currentWarehouseAddress() {

        return ADDRESSES[
                Random.from(new SecureRandom())
                        .nextInt(0, ADDRESSES.length)
                ];
    }
}
