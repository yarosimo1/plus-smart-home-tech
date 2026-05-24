package ru.yandex.practicum.shoppingstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ShoppingStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingStoreApplication.class, args);
    }
}
