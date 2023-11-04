package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class UserServiceImplIntegratedTest {
    @Autowired
    private UserService userService;

    @Test
    void updateUser() {
        UserDto user = UserDto.builder()
                .email("Email@email.com")
                .name("Name")
                .build();
        UserDto updatedUser = UserDto.builder()
                .email("NewEmail@email.com")
                .name("NewName")
                .build();

        userService.createUser(user);

        UserDto userDto = userService.getUserById(1L);

        assertEquals(1, userDto.getId());
        assertEquals("Email@email.com", userDto.getEmail());
        assertEquals("Name", userDto.getName());

        UserDto updatedUserDto = userService.updateUser(1L, updatedUser);

        assertEquals(1, updatedUserDto.getId());
        assertEquals("NewEmail@email.com", updatedUserDto.getEmail());
        assertEquals("NewName", updatedUserDto.getName());
    }
}