# Plus Smart Home Tech

Микросервисный проект для «умного дома» и e-commerce-сценариев: сбор телеметрии устройств, обработка сценариев, каталог товаров, корзина, склад, оформление заказа, оплата и доставка.

## Структура проекта

- `telemetry/` — сервисы телеметрии умного дома:
    - `collector` — принимает события датчиков и хабов, публикует их в Kafka;
    - `aggregator` — агрегирует события датчиков в снапшоты;
    - `analyzer` — анализирует снапшоты и события хабов, запускает сценарии;
    - `serialization` — Avro/Protobuf-схемы, сериализаторы и десериализаторы.
- `commerce/` — сервисы интернет-магазина:
    - `shopping-store` — каталог товаров;
    - `shopping-cart` — корзина покупателя;
    - `warehouse` — склад, бронирование и отгрузка товаров;
    - `order` — оформление и жизненный цикл заказа;
    - `payment` — расчёт стоимости и платежи;
    - `delivery` — планирование и расчёт доставки;
    - `interaction-api` — общие DTO и исключения для commerce-сервисов.
- `infra/` — инфраструктурные сервисы Spring Cloud:
    - `config-server` — централизованная конфигурация сервисов;
    - `discovery-server` — Eureka service discovery;
    - `gateway-server` — API Gateway.
- `hub-router/` — вспомогательные скрипты для запуска проверок telemetry-сценариев.
- `compose.yaml` — Kafka и PostgreSQL-базы для локального запуска.

## Требования

- Java 21
- Maven 3.9+
- Docker и Docker Compose

## Локальный запуск

1. Соберите проект:

   ```bash
   mvn clean package
   ```

2. Запустите инфраструктурные зависимости:

   ```bash
   docker compose up -d
   ```

   Compose поднимает Kafka и PostgreSQL-базы для commerce-сервисов. Порты по умолчанию:

   | Сервис | Порт |
      | --- | --- |
   | Kafka | `9092` |
   | shopping-store-db | `5433` |
   | shopping-cart-db | `5434` |
   | warehouse-db | `5435` |
   | order-db | `5436` |
   | payment-db | `5437` |
   | delivery-db | `5438` |

3. Запустите инфраструктурные Spring Cloud сервисы:

   ```bash
   mvn -pl infra/config-server spring-boot:run
   mvn -pl infra/discovery-server spring-boot:run
   mvn -pl infra/gateway-server spring-boot:run
   ```

4. Запустите нужные прикладные сервисы, например commerce-сервисы:

   ```bash
   mvn -pl commerce/shopping-store spring-boot:run
   mvn -pl commerce/shopping-cart spring-boot:run
   mvn -pl commerce/warehouse spring-boot:run
   mvn -pl commerce/order spring-boot:run
   mvn -pl commerce/payment spring-boot:run
   mvn -pl commerce/delivery spring-boot:run
   ```

5. Для telemetry-сервисов запустите необходимые модули:

   ```bash
   mvn -pl telemetry/collector spring-boot:run
   mvn -pl telemetry/aggregator spring-boot:run
   mvn -pl telemetry/analyzer spring-boot:run
   ```

## Конфигурация

Основные настройки сервисов лежат в `infra/config-server/src/main/resources/config`. Для локального запуска значения по умолчанию уже указывают на сервисы из `compose.yaml`. При необходимости их можно переопределить переменными окружения, например:

- `SHOPPING_STORE_DB_URL`, `SHOPPING_STORE_DB_USERNAME`, `SHOPPING_STORE_DB_PASSWORD`
- `WAREHOUSE_DB_URL`, `WAREHOUSE_DB_USERNAME`, `WAREHOUSE_DB_PASSWORD`
- `ORDER_DB_URL`, `PAYMENT_DB_URL`, `DELIVERY_DB_URL`
- `DELIVERY_BASE_COST`, `DELIVERY_WEIGHT_RATE`, `DELIVERY_VOLUME_RATE`
- `PAYMENT_VAT_RATE`

## Проверки

Запуск всех тестов:

```bash
mvn test
```

Скрипты для проверок hub-router находятся в `hub-router/run-tests.sh` и `hub-router/run-tests.bat`.

## Остановка локального окружения

```bash
docker compose down
```

Чтобы удалить данные локальных PostgreSQL-баз:

```bash
docker compose down -v
```
