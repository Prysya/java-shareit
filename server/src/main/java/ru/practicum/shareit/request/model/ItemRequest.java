package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@Setter
@Getter
@Table(name = "requests")
@NoArgsConstructor
@ToString
public class ItemRequest {
    /**
     * Уникальный идентификатор запроса
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Дата и время создания запроса
     */
    @Column
    private LocalDateTime created;
    /**
     * Текст запроса, содержащий описание требуемой вещи
     */
    @Column(nullable = false, length = 512)
    private String description;
    /**
     * {@link User}, создавший запрос
     */
    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requestor;
}
