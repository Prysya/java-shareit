package ru.practicum.shareit.booking.exception;

import ru.practicum.shareit.booking.constants.BookingState;


public class OutOfStateInStrategyException extends RuntimeException {
    public OutOfStateInStrategyException(String strategyName, BookingState state) {
        super(String.format("Отсутствует реализация стратегии=%s для state=%s", strategyName, state.name()));
    }
}
