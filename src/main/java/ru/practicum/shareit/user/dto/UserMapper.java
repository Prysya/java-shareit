package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {
    public UserDTO toDto(User user) {
        return UserDTO.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .build();
    }

    public User toUser(UserDTO userDTO) {
        return User.builder()
            .id(userDTO.getId())
            .name(userDTO.getName())
            .email(userDTO.getEmail())
            .build();
    }
}
