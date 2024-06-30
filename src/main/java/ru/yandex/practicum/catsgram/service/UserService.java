package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> getUsers() {
        return users.values();
    }

    public User getUserById(Long id) {
        if (findUserById(id).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return users.get(id);
    }

    public User createUser(User newUser) {
        if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        if (users.values().stream().anyMatch(user -> user.getEmail().equals(newUser.getEmail()))) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        newUser.setId(getNextId());
        newUser.setRegistrationDate(Instant.now());
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    public User updateUser(User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.values().stream().anyMatch(user -> user.getEmail().equals(newUser.getEmail()))) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if ((newUser.getEmail() == null || newUser.getEmail().isBlank()) ||
                    (newUser.getUsername() == null || newUser.getUsername().isBlank()) ||
                    (newUser.getPassword() == null || newUser.getPassword().isBlank())) {
                return oldUser;
            }
            oldUser.setEmail(newUser.getEmail());
            oldUser.setUsername(newUser.getUsername());
            oldUser.setPassword(newUser.getPassword());
            return oldUser;
        }
        throw new NotFoundException(String.format("Пользователя с id = %s не найдено", newUser.getId()));
    }

    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
