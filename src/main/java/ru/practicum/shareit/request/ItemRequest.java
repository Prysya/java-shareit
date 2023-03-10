package ru.practicum.shareit.request;

import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
@Getter
@Table(name = "requests")
@NoArgsConstructor
public class ItemRequest {
    /**
     * Дата и время создания запроса
     */
    @Column
    private final Instant created = Instant.now();
    /**
     * Уникальный идентификатор запроса
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
