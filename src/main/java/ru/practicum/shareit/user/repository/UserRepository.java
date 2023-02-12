package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAllUsers();

    User saveUser(User user);

    Optional<User> getUserById(Long id);

    User updateUser(Long userId, User user);

    boolean deleteUser(Long id);
}