package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EntityNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long vacantId = 1;

    @Override
    public User createUser(User user) {
        user.setId(vacantId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new EntityNotFoundException(
                    String.format("Пользователь с id = %s не найден!", userId),
                    User.class.getName());
        }
    }

    @Override
    public User updateUser(Long userId, User newUser) {
        users.put(userId, newUser);
        return newUser;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
