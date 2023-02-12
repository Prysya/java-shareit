package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * Статус бронирования
 */
enum Status {
    /**
     * новое бронирование, ожидает одобрения
     */
    WAITING,
    /**
     * бронирование подтверждено владельцем
     */
    APPROVED,
    /**
     * бронирование отклонено владельцем
     */
    REJECTED,
    /**
     * бронирование отменено создателем
     */
    CANCELED
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    /**
     * Уникальный идентификатор бронирования
     */
    Long id;
    /**
     * Дата и время начала бронирования
     */
    LocalDateTime start;
    /**
     * Дата и время конца бронирования
     */
    LocalDateTime end;
    /**
     * Вещь, которую пользователь бронирует
     */
    Item item;
    /**
     * Пользователь, который осуществляет бронирование
     */
    User booker;
    /**
     * {@link Status}
     */
    Status status;
}