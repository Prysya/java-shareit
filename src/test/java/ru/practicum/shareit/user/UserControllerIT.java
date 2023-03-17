package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
class UserControllerIT {
    private static final String CONTROLLER_URL = "/users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    /**
     * getAllUsers
     */
    @Test
    @SneakyThrows
    void getAllUsers_whenInvoked_thenReturnedStatusOk() {
        mockMvc
            .perform(
                get(CONTROLLER_URL)
            )
            .andExpect(status().isOk());

        verify(userService).getAllUsers();
    }

    /**
     * getAllUsers
     */
    @Test
    @SneakyThrows
    void getUserById_whenUserIdIsValid_thenReturnedUserAndStatusOk() {
        long userId = 1L;

        User user = User.builder().id(userId).build();
        UserDTO userDTO = UserMapper.toDto(user);

        when(userService.getUserById(userId)).thenReturn(userDTO);

        String json = mockMvc
            .perform(
                get(CONTROLLER_URL + "/{userId}", userId)
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDTO), json);
        assertEquals(userId, userDTO.getId());
    }

    @Test
    @SneakyThrows
    void getUserById_whenUserIdIsNotValid_thenReturnedBadRequest() {
        String userId = "userId";

        mockMvc.perform(
                get(CONTROLLER_URL + "/{userId}", userId)
            )
            .andExpect(status().isBadRequest());

        verify(userService, never()).getUserById(anyLong());
    }

    /**
     * getAllUsers
     */
    @Test
    @SneakyThrows
    void saveUser_whenUserIsValid_thenReturnedUserAndStatusCreated() {
        UserDTO userDTO = UserDTO.builder().email("test@test.com").name("name").build();

        when(userService.saveUser(any(UserDTO.class))).thenReturn(userDTO);

        String json = mockMvc
            .perform(
                post(CONTROLLER_URL)
                    .content(objectMapper.writeValueAsString(userDTO))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name", is(userDTO.getName())))
            .andExpect(jsonPath("$.email", is(userDTO.getEmail())))
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDTO), json);
    }

    @Test
    @SneakyThrows
    void saveUser_whenUserIsNotValid_thenReturnedBadRequest() {
        UserDTO userDTO = UserDTO.builder().build();

        mockMvc
            .perform(
                post(CONTROLLER_URL)
                    .content(objectMapper.writeValueAsString(userDTO))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest());

        verify(userService, never()).saveUser(any(UserDTO.class));
    }

    /**
     * getAllUsers
     */
    @Test
    @SneakyThrows
    void updateUser_whenUserIsValid_thenReturnedUserAndStatusOk() {
        long userId = 1L;
        UserDTO userDTO = UserDTO.builder().id(userId).build();

        when(userService.updateUser(eq(userId), any(UserDTO.class))).thenReturn(userDTO);

        String json = mockMvc
            .perform(
                patch(CONTROLLER_URL + "/{userId}", userId)
                    .content(objectMapper.writeValueAsString(userDTO))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(userDTO.getId()), Long.class))
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDTO), json);
    }

    @Test
    @SneakyThrows
    void updateUser_whenUserIsNotValid_thenReturnedBadRequest() {
        long userId = 1L;
        UserDTO userDTO = UserDTO.builder().id(userId).email("not valid email").build();

        mockMvc
            .perform(
                patch(CONTROLLER_URL + "/{userId}", userId)
                    .content(objectMapper.writeValueAsString(userDTO))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(anyLong(), any(UserDTO.class));
    }

    /**
     * getAllUsers
     */
    @Test
    @SneakyThrows
    void deleteUser_whenUserIdIsValid_thenReturnedStatusOk() {
        long userId = 1L;

        mockMvc
            .perform(
                delete(CONTROLLER_URL + "/{userId}", userId)
            )
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void deleteUser_whenUserIdIsNotValid_thenReturnedBadRequest() {
        String userId = "string";

        mockMvc
            .perform(
                delete(CONTROLLER_URL + "/{userId}", userId)
            )
            .andExpect(status().isBadRequest());
    }
}