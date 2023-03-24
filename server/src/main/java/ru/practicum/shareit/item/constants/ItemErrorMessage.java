package ru.practicum.shareit.item.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ItemErrorMessage {
    public static final String NOT_FOUND = "Вещь c id: '%d' не найдена";
    public static final String UNAUTHORIZED = "Вещь с id: '%d', не принадлежит пользователю с id: '%d";
    public static final String COMMENT_ERROR =
        "Пользователь с id: '%d' не брал вещь с id: '%d' в аренду, либо аренда еще не завершена";
}
