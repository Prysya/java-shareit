package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    public static final String NOT_FOUND = "Пользователь с id: '%d' не найден";
    public static final String EMAIL_ALREADY_CREATED = "Пользователь с таким email уже зарегистрирован";
    private final UserRepository repository;

    @Override
    public List<User> getAllUsers() {
        return repository.getAllUsers();
    }

    @Override
    public User saveUser(User user) {
        User newUser = repository.saveUser(user);

        if (Objects.isNull(newUser)) {
            throw new ConflictException(EMAIL_ALREADY_CREATED);
        }

        return user;
    }

    @Override
    public User getUserById(Long userId) {
        return repository.getUserById(userId)
            .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND, userId)));
    }

    @Override
    public User updateUser(Long userId, User user) {
        getUserById(userId);

        User updatedUser = repository.updateUser(userId, user);

        if (Objects.isNull(updatedUser)) {
            throw new ConflictException(EMAIL_ALREADY_CREATED);
        }

        return updatedUser;
    }

    @Override
    public void deleteUser(Long userId) {
        repository.deleteUser(userId);
    }
}