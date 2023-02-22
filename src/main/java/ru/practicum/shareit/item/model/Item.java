package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    /**
     * Уникальный идентификатор вещи
     */
    private Long id;
    /**
     * Краткое название
     */
    private String name;
    /**
     * Развёрнутое описание
     */
    private String description;
    /**
     * Статус о том, доступна или нет вещь для аренды
     */
    private Boolean available;
    /**
     * {@link User} - владелец вещи
     */
    private Long owner;
    /**
     * {@link ItemRequest}
     * Если вещь была создана по запросу другого пользователя, то в этом
     * поле будет храниться ссылка на соответствующий запрос
     */
    private ItemRequest request;
}
