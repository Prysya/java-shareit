package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Test
    void getAllUsers_whenInvoked_thenResponseStatusOkAndListOfUsersInBody() {
        List<UserDTO> usersList = List.of(UserDTO.builder().id(1L).build(), UserDTO.builder().id(2L).build());

        when(userService.getAllUsers())
            .thenReturn(usersList);

        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usersList, response.getBody());
    }

    @Test
    void getUserById_whenInvoked_thenResponseStatusOkAndUserInBody() {
        long userId = 1L;
        UserDTO userDTO = UserDTO.builder().id(userId).build();

        when(userService.getUserById(anyLong())).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.getUserById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    void saveUser_whenInvoked_thenResponseStatusCreatedAndListOfUsersInBody() {
        UserDTO userDTO = UserDTO.builder().build();

        when(userService.saveUser(userDTO)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.saveUser(userDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    void updateUser_whenInvoked_thenResponseStatusOkAndUserInBody() {
        long userId = 1L;
        UserDTO userDTO = UserDTO.builder().id(userId).build();

        when(userService.updateUser(userId, userDTO)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.updateUser(userDTO, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    void deleteUser_whenInvoked_thenResponseStatusOk() {
        long userId = 1L;

        ResponseEntity<Void> response = userController.deleteUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}