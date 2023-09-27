package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User getUserById(Long userId);

    User updateUser(Long userId, User newUser);

    List<User> getAllUsers();
}
