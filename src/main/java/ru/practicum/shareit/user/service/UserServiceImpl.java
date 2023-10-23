package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User updatedUser = validateUserById(userId);
        if (!updatedUser.getEmail().equals(userDto.getEmail())) validateEmail(userDto);
        if (userDto.getName() != null) updatedUser.setName(userDto.getName());
        if (userDto.getEmail() != null) updatedUser.setEmail(userDto.getEmail());
        return UserMapper.toUserDto(userRepository.save(updatedUser));
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.toUserDto(validateUserById(userId));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.toUserDtoList(userRepository.findAll());
    }

    @Override
    public void deleteUserById(Long userId) {
        validateUserById(userId);
        userRepository.deleteById(userId);
    }

    private void validateEmail(UserDto userDto) {
        if (getAllUsers().stream().anyMatch(u -> u.getEmail().equals(userDto.getEmail()))) {
            throw new EmailAlreadyExistsException(
                    String.format("Пользователь с Email = %s уже существует!",
                            userDto.getEmail()));
        }
    }

    private User validateUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Пользователь с id = %s не найден!", userId),
                User.class.getName()));
    }
}
