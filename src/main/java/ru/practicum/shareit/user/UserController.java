package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return userMapper.toDto(userService.getUserById(id));
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserDTO saveUser(@Validated(UserDTO.New.class) @RequestBody UserDTO userDTO) {
        return userMapper.toDto(userService.saveUser(userMapper.toUser(userDTO)));
    }

    @PatchMapping("/{userId}")
    public UserDTO updateUser(
        @Validated({UserDTO.Update.class}) @RequestBody UserDTO userDTO, @PathVariable Long userId
    ) {
        return userMapper.toDto(userService.updateUser(userId, userMapper.toUser(userDTO)));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}