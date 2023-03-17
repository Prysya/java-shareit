package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.booking.constants.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Builder
@AllArgsConstructor
@Setter
@Getter
@Table(name = "bookings")
@NoArgsConstructor
@ToString
public class Booking {
    /**
     * Уникальный идентификатор бронирования
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Дата и время начала бронирования
     */
    @Column(name = "start_date")
    private LocalDateTime start;

    /**
     * Дата и время конца бронирования
     */
    @Column(name = "end_date")
    private LocalDateTime end;

    /**
     * Вещь, которую пользователь бронирует
     */
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    /**
     * Пользователь, который осуществляет бронирование
     */
    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;

    /**
     * {@link BookingStatus}
     */
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}