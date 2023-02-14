package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, Long> emails = new HashMap<>();

    @Override
    public List<User> getAllUsers() {
        return users.values().stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public User saveUser(User user) {
        if (emails.containsKey(user.getEmail())) {
            return null;
        }

        Long id = Integer.toUnsignedLong(users.size()) + 1;
        user.setId(id);

        users.put(id, user);
        emails.put(user.getEmail(), id);

        return user;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User updateUser(Long userId, User user) {
        User oldUser = users.get(userId);
        emails.remove(oldUser.getEmail());

        if (emails.containsKey(user.getEmail()) && !oldUser.getEmail().equals(user.getEmail())) {
            return null;
        }


        User newUser = User.builder()
            .id(userId)
            .name(Objects.requireNonNullElse(user.getName(), oldUser.getName()))
            .email(Objects.requireNonNullElse(user.getEmail(), oldUser.getEmail()))
            .build();

        users.put(userId, newUser);
        emails.put(newUser.getEmail(), userId);

        return newUser;
    }

    @Override
    public boolean deleteUser(Long id) {
        User user = users.get(id);

        if (Objects.isNull(user)) {
            return false;
        }

        emails.remove(user.getEmail());
        users.put(id, null);
        return true;
    }
}