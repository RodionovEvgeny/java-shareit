package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User getUserById(Long userId);

    User updateUser(User updatedUser);

    List<User> getAllUsers();

    void deleteUserById(Long userId);
}
