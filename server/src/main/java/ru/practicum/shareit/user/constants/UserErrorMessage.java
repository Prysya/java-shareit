package ru.practicum.shareit.user.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserErrorMessage {
    public static final String NOT_FOUND = "Пользователь с id: '%d' не найден";
    public static final String EMAIL_ALREADY_CREATED = "Пользователь с таким email уже зарегистрирован";
}
