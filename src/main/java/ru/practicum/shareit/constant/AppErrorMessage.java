package ru.practicum.shareit.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AppErrorMessage {
    public static final String PAGE_IS_NOT_POSITIVE = "Текущая страница не может иметь отрицательное значение";
    public static final String SIZE_IS_NOT_POSITIVE = "Количество элементов должно быть больше нуля";
}
