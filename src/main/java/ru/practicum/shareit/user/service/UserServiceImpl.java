package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class UserServiceImpl implements UserService {
    public static final String NOT_FOUND = "Пользователь с id: '%d' не найден";
    public static final String EMAIL_ALREADY_CREATED = "Пользователь с таким email уже зарегистрирован";
    private final UserRepository repository;
    private final UserMapper userMapper;


    @Override
    public List<UserDTO> getAllUsers() {
        return repository.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO saveUser(UserDTO userDTO) {
        try {
            User newUser = repository.save(userMapper.toUser(userDTO));
            return userMapper.toDto(newUser);
        } catch (Exception e) {
            throw new ConflictException(EMAIL_ALREADY_CREATED);
        }
    }

    @Override
    public UserDTO getUserById(Long userId) {
        return userMapper.toDto(
            repository.findById(userId).orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND, userId))));
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        User oldUser =
            repository.findById(userId).orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND, userId)));

        User updatedUser = User.builder()
            .id(userId)
            .name(Objects.requireNonNullElse(userDTO.getName(), oldUser.getName()))
            .email(Objects.requireNonNullElse(userDTO.getEmail(), oldUser.getEmail()))
            .build();

        return saveUser(userMapper.toDto(updatedUser));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        repository.deleteById(userId);
    }
}