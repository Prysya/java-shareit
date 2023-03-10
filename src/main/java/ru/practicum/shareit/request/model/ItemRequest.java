package ru.practicum.shareit.request.model;

import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Builder
@AllArgsConstructor
@Setter
@Getter
@Table(name = "requests")
@NoArgsConstructor
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        ItemRequest that = (ItemRequest) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
