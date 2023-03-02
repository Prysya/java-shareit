package ru.practicum.shareit.booking.constants;

public enum BookingState {
    /**
     * Все
     */
    ALL,
    /**
     * Текущие
     */
    CURRENT,
    /**
     * Завершённые
     */
    PAST,
    /**
     * Будущие
     */
    FUTURE,
    /**
     * Ожидающие подтверждения
     */
    WAITING,
    /**
     * Отклоненные
     */
    REJECTED
}
