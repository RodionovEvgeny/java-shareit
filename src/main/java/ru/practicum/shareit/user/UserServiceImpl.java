package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userStorage.createUser(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User newUser = userStorage.getUserById(userId);
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
}
