package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    /**
     * Уникальный идентификатор бронирования
     */
    private Long id;
    /**
     * Дата и время начала бронирования
     */
    private LocalDateTime start;
    /**
     * Дата и время конца бронирования
     */
    private LocalDateTime end;
    /**
     * Вещь, которую пользователь бронирует
     */
    private Item item;
    /**
     * Пользователь, который осуществляет бронирование
     */
    private User booker;
    /**
     * {@link Status}
     */
    private Status status;
}