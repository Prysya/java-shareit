package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.constants.UserErrorMessage;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDTO userDTO;

    @BeforeEach
    public void beforeEach() {
        userDTO = UserDTO.builder().id(1L).build();
    }

    /**
     * getAllUsers
     */
    @Test
    void getAllUsers_whenInvoked_thenReturnedListOfUsers() {
        List<User> usersList = List.of(UserMapper.toUser(userDTO));

        when(repository.findAll()).thenReturn(usersList);

        List<UserDTO> users = userService.getAllUsers();

        assertEquals(List.of(userDTO), users);
    }

    /**
     * saveUser
     */
    @Test
    void saveUser_whenItemIsSaved_thenReturnedUser() {
        when(repository.save(any(User.class))).thenReturn(UserMapper.toUser(userDTO));

        UserDTO savedUser = userService.saveUser(userDTO);

        assertEquals(userDTO, savedUser);
    }

    @Test
    void saveUser_whenRepositoryThrownError_thenConflictExceptionThrown() {
        when(repository.save(any(User.class))).thenThrow(
            new RuntimeException()
        );

        ConflictException exception = assertThrows(
            ConflictException.class, () -> userService.saveUser(userDTO)
        );

        assertEquals(UserErrorMessage.EMAIL_ALREADY_CREATED, exception.getMessage());
    }

    /**
     * getUserById
     */
    @Test
    void getUserById_whenUserIsFound_thenReturnedUser() {
        long userId = 1L;

        when(repository.findById(userId)).thenReturn(Optional.of(UserMapper.toUser(userDTO)));

        UserDTO userById = userService.getUserById(userId);

        assertEquals(userDTO, userById);
    }

    @Test
    void getUserById_whenUserIsNotFound_thenUserNotFoundThrown() {
        long userId = 1L;

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }

    /**
     * updateUser
     */
    @Test
    void updateUser_whenUserIsFound_thenUpdateAndReturnUser() {
        long userId = 1L;
        UserDTO newUser = UserDTO.builder().email("new email").name("new name").build();

        when(repository.findById(userId)).thenReturn(Optional.of(UserMapper.toUser(userDTO)));
        when(repository.save(any(User.class))).thenReturn(UserMapper.toUser(newUser));

        UserDTO updatedUser = userService.updateUser(userId, newUser);

        assertEquals(newUser.getName(), updatedUser.getName());
        assertEquals(newUser.getEmail(), updatedUser.getEmail());
    }

    /**
     * deleteUser
     */
    @Test
    void deleteUser_whenInvokedWithoutErrors_thenNotThrown() {
        assertDoesNotThrow(() -> userService.deleteUser(userDTO.getId()));
    }
}