package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDTO;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get all users");
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Get user by id with userId={}", userId);
        return userClient.getUserById(userId);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<Object> saveUser(@Validated(UserDTO.New.class) @RequestBody UserDTO userDTO) {
        log.info("Save user with user={}", userDTO);
        return userClient.saveUser(userDTO);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
        @Validated({UserDTO.Update.class}) @RequestBody UserDTO userDTO, @PathVariable Long userId
    ) {
        log.info("Update user with userId={}, user={}", userId, userDTO);
        return userClient.updateUser(userId, userDTO);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("Delete user with userId={}", userId);
        return userClient.deleteUser(userId);
    }
}