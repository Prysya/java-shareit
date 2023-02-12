package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    /**
     * Уникальный идентификатор запроса
     */
    Long id;
    /**
     * Текст запроса, содержащий описание требуемой вещи
     */
    String description;
    /**
     * {@link User}, создавший запрос
     */
    User requestor;
    /**
     * Дата и время создания запроса
     */
    LocalDateTime created;
}
