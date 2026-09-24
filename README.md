# Plus Smart Home Tech

Многомодульное приложение, объединяющее обработку телеметрии умного дома и e-commerce сценарии.

Проект демонстрирует использование **Kafka, event-driven architecture, Avro, Protobuf, gRPC и Spring Cloud** в распределённой системе.

**Status:** Completed

---

## Tech Stack

### Backend

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.2-brightgreen?logo=springboot)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.3-brightgreen?logo=spring)

* Spring Boot
* Spring Cloud
* Spring Data JPA
* Spring Web
* Spring Cloud Gateway
* Eureka
* Config Server

### Messaging & Serialization

![Kafka](https://img.shields.io/badge/Apache%20Kafka-black?logo=apachekafka)
![Avro](https://img.shields.io/badge/Apache%20Avro-red)
![gRPC](https://img.shields.io/badge/gRPC-4285F4?logo=grpc)

* Apache Kafka
* Kafka Clients
* Avro
* Protocol Buffers
* gRPC

### Data & Infrastructure

![PostgreSQL](https://img.shields.io/badge/PostgreSQL-blue?logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-blue?logo=docker)
![Maven](https://img.shields.io/badge/Maven-C71A36?logo=apachemaven)

* PostgreSQL
* Hibernate / JPA
* Docker
* Docker Compose
* Maven
* Springdoc OpenAPI

---

## About the Project

Проект состоит из двух основных функциональных областей:

```text
┌─────────────────────────────────────┐
│         Plus Smart Home Tech        │
├─────────────────┬───────────────────┤
│ Smart Home      │ E-commerce        │
│ Telemetry       │                   │
└─────────────────┴───────────────────┘
```

Первая часть отвечает за обработку событий от устройств умного дома.

Вторая часть моделирует e-commerce систему с отдельными сервисами для магазина, корзины, склада, заказов, оплаты и доставки.

Такое разделение позволяет продемонстрировать несколько подходов к взаимодействию сервисов в одном многомодульном проекте.

---

# Smart Home Telemetry

## Architecture

Основной поток обработки телеметрии построен вокруг Kafka.

```text
Sensors / Hubs
      │
      ▼
┌──────────────┐
│   Collector  │
└──────┬───────┘
       │
       │ Kafka
       ▼
┌──────────────┐
│    Kafka     │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│  Aggregator  │
└──────┬───────┘
       │
       │ Snapshots
       ▼
┌──────────────┐
│   Analyzer   │
└──────┬───────┘
       │
       │ gRPC
       ▼
┌──────────────┐
│  Hub Router  │
└──────────────┘
```

---

## Collector

Получает события от датчиков и хабов и публикует их в Kafka.

Основные Kafka topics:

```text
telemetry.sensors.v1
telemetry.hubs.v1
```

---

## Aggregator

Обрабатывает события датчиков и формирует агрегированные snapshots.

Результат публикуется в:

```text
telemetry.snapshots.v1
```

---

## Analyzer

Анализирует snapshots и события хабов.

На основе полученных данных сервис выполняет сценарии автоматизации умного дома.

Для взаимодействия с Hub Router используется **gRPC**.

---

## Serialization

Отдельный модуль отвечает за схемы и сериализацию сообщений.

Используются:

* Apache Avro;
* Protocol Buffers;
* Kafka serialization/deserialization.

Это позволяет отделить формат сообщений от бизнес-логики сервисов.

---

# E-commerce

Вторая часть проекта представляет набор взаимодействующих сервисов.

```text
                 ┌──────────────┐
                 │   Gateway    │
                 └──────┬───────┘
                        │
        ┌───────────────┼────────────────┐
        │               │                │
        ▼               ▼                ▼
   Shopping Store   Shopping Cart    Warehouse
        │               │                │
        └───────────────┼────────────────┘
                        │
                        ▼
                      Order
                        │
              ┌─────────┴─────────┐
              ▼                   ▼
           Payment             Delivery
```

### Services

* `shopping-store` — работа с товарами;
* `shopping-cart` — корзина пользователя;
* `warehouse` — складские операции;
* `order` — работа с заказами;
* `payment` — обработка оплаты;
* `delivery` — доставка;
* `interaction-api` — контракты взаимодействия между сервисами.

---

# Infrastructure

Проект использует Spring Cloud инфраструктуру:

```text
┌──────────────────┐
│   API Gateway    │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ Eureka Discovery │
└──────────────────┘

┌──────────────────┐
│  Config Server   │
└──────────────────┘
```

Используются:

* Spring Cloud Gateway;
* Eureka Discovery Server;
* Spring Cloud Config Server.

---

# Project Structure

```text
plus-smart-home-tech/
│
├── telemetry/
│   ├── collector/
│   ├── aggregator/
│   ├── analyzer/
│   └── serialization/
│
├── commerce/
│   ├── shopping-store/
│   ├── shopping-cart/
│   ├── warehouse/
│   ├── order/
│   ├── payment/
│   ├── delivery/
│   └── interaction-api/
│
├── infra/
│   ├── config-server/
│   ├── discovery-server/
│   └── gateway-server/
│
├── hub-router/
│
├── compose.yaml
└── pom.xml
```

---

# Key Technical Decisions

### Event-Driven Architecture

Обмен событиями между компонентами telemetry реализован через Kafka.

```text
Producer → Kafka → Consumer
```

Это позволяет отделить producer и consumer и использовать асинхронное взаимодействие.

### Schema-Based Serialization

Для сообщений используются Avro и Protobuf.

Схемы позволяют явно определить структуру передаваемых данных.

### gRPC

gRPC используется для взаимодействия Analyzer с Hub Router.

```text
Analyzer
   │
   │ gRPC
   ▼
Hub Router
```

### Microservices

E-commerce часть разделена на сервисы по отдельным бизнес-областям.

### Database per Service

Для e-commerce сервисов используются отдельные PostgreSQL базы данных.

---

# Running the Project

Для запуска инфраструктуры используется Docker Compose.

Основные компоненты:

```text
Kafka
PostgreSQL
Spring Cloud services
Application services
```

Запуск:

```bash
docker compose up
```

---

# What This Project Demonstrates

* event-driven architecture;
* Apache Kafka;
* producer / consumer interaction;
* Avro;
* Protocol Buffers;
* gRPC;
* Spring Boot;
* Spring Cloud;
* Eureka;
* Config Server;
* API Gateway;
* микросервисную декомпозицию;
* PostgreSQL;
* JPA / Hibernate;
* Docker Compose;
* Maven;
* работу с несколькими независимыми бизнес-доменами.
