package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    public static final String NOT_FOUND = "Пользователь с id: '%d' не найден";
    public static final String EMAIL_ALREADY_CREATED = "Пользователь с таким email уже зарегистрирован";
    private final UserRepository repository;
    private final UserMapper userMapper;


    @Override
    public List<UserDTO> getAllUsers() {
        return repository.getAllUsers().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        User newUser = repository.saveUser(userMapper.toUser(userDTO));

        if (Objects.isNull(newUser)) {
            throw new ConflictException(EMAIL_ALREADY_CREATED);
        }

        return userMapper.toDto(newUser);
    }

    @Override
    public UserDTO getUserById(Long userId) {
        return userMapper.toDto(repository.getUserById(userId)
            .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND, userId))));
    }

    @Override
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        getUserById(userId);

        User updatedUser = repository.updateUser(userId, userMapper.toUser(userDTO));

        if (Objects.isNull(updatedUser)) {
            throw new ConflictException(EMAIL_ALREADY_CREATED);
        }

        return userMapper.toDto(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        repository.deleteUser(userId);
    }
}