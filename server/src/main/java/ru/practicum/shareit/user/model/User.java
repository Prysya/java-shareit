package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@Setter
@Getter
@Table(name = "users")
@NoArgsConstructor
@ToString
public class User {
    /**
     * Уникальный идентификатор пользователя
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    /**
     * Имя или логин пользователя
     */
    @Column(nullable = false)
    private String name;

    /**
     * Адрес электронной почты
     */
    @Column(nullable = false, unique = true, length = 512)
    private String email;
}
