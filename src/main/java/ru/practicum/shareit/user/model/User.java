package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    /**
     * Уникальный идентификатор пользователя
     */
    private Long id;
    /**
     * Имя или логин пользователя
     */
    private String name;
    /**
     * Адрес электронной почты
     */
    private String email;
}
