package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.constants.UserErrorMessage;
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
public
class UserServiceImpl implements UserService {

    private final UserRepository repository;


    @Override
    public List<UserDTO> getAllUsers() {
        return repository.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO saveUser(UserDTO userDTO) {
        try {
            User newUser = repository.save(UserMapper.toUser(userDTO));
            return UserMapper.toDto(newUser);
        } catch (Exception e) {
            throw new ConflictException(UserErrorMessage.EMAIL_ALREADY_CREATED);
        }
    }

    @Override
    public UserDTO getUserById(Long userId) {
        return UserMapper.toDto(findAndReturnUser(userId));
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        User oldUser = findAndReturnUser(userId);

        User updatedUser = User.builder()
            .id(userId)
            .name(Objects.requireNonNullElse(userDTO.getName(), oldUser.getName()))
            .email(Objects.requireNonNullElse(userDTO.getEmail(), oldUser.getEmail()))
            .build();

        return saveUser(UserMapper.toDto(updatedUser));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        repository.deleteById(userId);
    }

    private User findAndReturnUser(Long userId) {
        return repository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format(UserErrorMessage.NOT_FOUND, userId)));
    }
}