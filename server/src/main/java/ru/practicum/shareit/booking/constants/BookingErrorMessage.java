package ru.practicum.shareit.booking.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BookingErrorMessage {
    public static final String USER_OWN_ITEM = "Пользователь не может создать бронирование на свою же вещь";
    public static final String BOOKING_NOT_FOUND = "Бронирование с id = %d, не найдено";
    public static final String STATUS_ALREADY_CHANGED =
        "Невозможно изменить статус бронирования с id = %d. Статус уже ранее был изменен";
    public static final String NOT_AUTHORIZED = "Изменение статуса вещи может быть выполнено только владельцем";
    public static final String ITEM_IS_NOT_AVAILABLE = "Вещь c id = %d не доступна";
}
