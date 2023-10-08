package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final ItemStorage itemStorage;
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
    public User updateUser(User updatedUser) {
        users.put(updatedUser.getId(), updatedUser);
        return updatedUser;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUserById(Long userId) {
        itemStorage.deleteItemsByOwnersId(userId);
        users.remove(userId);
    }
}
