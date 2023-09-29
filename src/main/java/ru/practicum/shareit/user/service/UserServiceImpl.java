package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto createUser(UserDto userDto) {
        validateEmail(userDto);
        User user = userStorage.createUser(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User newUser = userStorage.getUserById(userId);
        if (!newUser.getEmail().equals(userDto.getEmail())) validateEmail(userDto);
        if (userDto.getName() != null) newUser.setName(userDto.getName());
        if (userDto.getEmail() != null) newUser.setEmail(userDto.getEmail());
        return UserMapper.toUserDto(userStorage.updateUser(userId, newUser));
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.toUserDto(userStorage.getUserById(userId));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.toUserDto(userStorage.getAllUsers());
    }

    @Override
    public void deleteUserById(Long userId) {
        userStorage.getUserById(userId);
        userStorage.deleteUserById(userId);
    }

    private void validateEmail(UserDto userDto) {
        if (getAllUsers().stream().anyMatch(u -> u.getEmail().equals(userDto.getEmail()))) {
            throw new EmailAlreadyExistsException(
                    String.format("Пользователь с Email = %s уже существует!",
                            userDto.getEmail()));
        }
    }
}
