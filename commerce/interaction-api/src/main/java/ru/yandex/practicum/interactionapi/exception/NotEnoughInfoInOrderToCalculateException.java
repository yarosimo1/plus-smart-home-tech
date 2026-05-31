package ru.yandex.practicum.interactionapi.exception;

import org.springframework.http.HttpStatus;

public class NotEnoughInfoInOrderToCalculateException extends ApiException {
  public NotEnoughInfoInOrderToCalculateException(String message) {
    super(message, "Недостаточно информации в заказе для расчёта", HttpStatus.BAD_REQUEST);
  }
}
