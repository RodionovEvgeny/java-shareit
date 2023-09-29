package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User getUserById(Long userId);

    User updateUser(Long userId, User newUser);

    List<User> getAllUsers();

    void deleteUserById(Long userId);
}
