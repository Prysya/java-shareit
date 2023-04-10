package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDTO;

import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();

    UserDTO saveUser(UserDTO userDTO);

    UserDTO getUserById(Long id);

    UserDTO updateUser(Long userId, UserDTO userDTO);

    void deleteUser(Long id);
}