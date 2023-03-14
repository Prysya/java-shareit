package ru.practicum.shareit.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserMapper {
    public static UserDTO toDto(User user) {
        return UserDTO.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .build();
    }

    public static User toUser(UserDTO userDTO) {
        return User.builder()
            .id(userDTO.getId())
            .name(userDTO.getName())
            .email(userDTO.getEmail())
            .build();
    }
}
