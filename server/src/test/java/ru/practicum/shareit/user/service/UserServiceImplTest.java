package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(UserServiceImpl.class)
@AutoConfigureMockMvc
class UserServiceImplTest {
    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;

    @Test
    void createUser() {
        when(userRepository.save(any(User.class))).thenReturn(getUser());

        UserDto userDto = userService.createUser(getUserDtoRequest());

        assertEquals(1, userDto.getId());
        assertEquals("Email@email.com", userDto.getEmail());
        assertEquals("Name", userDto.getName());
    }

    @Test
    void updateUser() {
        User updatedUser = getUser();
        updatedUser.setEmail("newEmail@email.com");
        UserDto updatedUserDto = getUserDtoRequest();
        updatedUserDto.setEmail("newEmail@email.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(updatedUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        UserDto userDto = userService.updateUser(updatedUser.getId(), updatedUserDto);

        assertEquals(1, userDto.getId());
        assertEquals("newEmail@email.com", userDto.getEmail());
        assertEquals("Name", userDto.getName());
    }

    @Test
    void getUserById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(getUser()));
        UserDto userDto = userService.getUserById(anyLong());

        assertEquals(1, userDto.getId());
        assertEquals("Email@email.com", userDto.getEmail());
        assertEquals("Name", userDto.getName());
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(getUser()));
        List<UserDto> userDtos = userService.getAllUsers();

        assertEquals(1, userDtos.size());
        assertEquals(1, userDtos.get(0).getId());
        assertEquals("Email@email.com", userDtos.get(0).getEmail());
        assertEquals("Name", userDtos.get(0).getName());
    }

    @Test
    void deleteUserById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(getUser()));
        userService.deleteUserById(anyLong());
        verify(userRepository, times(1)).deleteById(anyLong());
    }

    private User getUser() {
        return User.builder()
                .id(1)
                .email("Email@email.com")
                .name("Name")
                .build();
    }

    private UserDto getUserDtoRequest() {
        return UserDto.builder()
                .email("Email@email.com")
                .name("Name")
                .build();
    }
}