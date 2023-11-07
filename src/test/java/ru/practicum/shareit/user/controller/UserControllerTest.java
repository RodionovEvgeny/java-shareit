package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    private final UserDto requestUser = UserDto.builder()
            .email("Email@email.com")
            .name("Name")
            .build();

    private final UserDto responseUser = UserDto.builder()
            .id(1L)
            .email("Email@email.com")
            .name("Name")
            .build();

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @Test
    void hasToCreateUser() throws Exception {
        when(userService.createUser(any(UserDto.class))).thenReturn(responseUser);
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(requestUser))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(responseUser)));
        verify(userService, times(1)).createUser(any(UserDto.class));
    }

    @Test
    void hasToUpdateUser() throws Exception {
        when(userService.updateUser(anyLong(), any(UserDto.class))).thenReturn(responseUser);
        mockMvc.perform(
                        patch("/users/1")
                                .content(objectMapper.writeValueAsString(requestUser))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(responseUser)));
        verify(userService, times(1)).updateUser(anyLong(), any(UserDto.class));
    }

    @Test
    void hasToGetUserById() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(responseUser);
        mockMvc.perform(
                        get("/users/1")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(responseUser)));
        verify(userService, times(1)).getUserById(anyLong());
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(responseUser));
        mockMvc.perform(
                        get("/users")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(responseUser))));
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void deleteUserById() throws Exception {
        mockMvc.perform(
                        delete("/users/1")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUserById(anyLong());
    }
}