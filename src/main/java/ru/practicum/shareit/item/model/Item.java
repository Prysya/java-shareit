package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    /**
     * Уникальный идентификатор вещи
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Краткое название
     */
    @Column(nullable = false)
    private String name;

    /**
     * Развёрнутое описание
     */
    @Column(nullable = false, length = 512)
    private String description;

    /**
     * Статус о том, доступна или нет вещь для аренды
     */
    @Column(name = "is_available", nullable = false)
    private Boolean available;

    /**
     * {@link User} - владелец вещи
     */
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    /**
     * {@link ItemRequest}
     * Если вещь была создана по запросу другого пользователя, то в этом
     * поле будет храниться ссылка на соответствующий запрос
     */
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}
