package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    private final ItemRequestDto requestItemRequest = ItemRequestDto.builder()
            .description("Description")
            .build();

    private final ItemRequestDto responseItemRequest = ItemRequestDto.builder()
            .id(1L)
            .description("Description")
            .created(LocalDateTime.now())
            .build();

    private final ItemRequestDtoWithAnswers responseItemRequestWithAnswers = ItemRequestDtoWithAnswers.builder()
            .id(1L)
            .description("Description")
            .created(LocalDateTime.now())
            .items(null)
            .build();
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RequestService requestService;

    @Test
    void hasToAddItemRequest() throws Exception {
        when(requestService.addItemRequest(anyLong(), any(ItemRequestDto.class))).thenReturn(responseItemRequest);
        mockMvc.perform(
                        post("/requests")
                                .content(objectMapper.writeValueAsString(requestItemRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(responseItemRequest)));
        verify(requestService, times(1)).addItemRequest(anyLong(), any(ItemRequestDto.class));
    }

    @Test
    void hasToGetOwnersItemRequests() throws Exception {
        when(requestService.getOwnersItemRequests(anyLong())).thenReturn(List.of(responseItemRequestWithAnswers));
        mockMvc.perform(
                        get("/requests")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(responseItemRequestWithAnswers))));
        verify(requestService, times(1)).getOwnersItemRequests(anyLong());
    }

    @Test
    void hasToGetAllItemRequests() throws Exception {
        when(requestService.getAllItemRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(responseItemRequestWithAnswers));
        mockMvc.perform(
                        get("/requests/all")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(responseItemRequestWithAnswers))));
        verify(requestService, times(1)).getAllItemRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getItemRequest() throws Exception {
        when(requestService.getItemRequest(anyLong(), anyLong())).thenReturn(responseItemRequestWithAnswers);
        mockMvc.perform(
                        get("/requests/1")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(responseItemRequestWithAnswers)));
        verify(requestService, times(1)).getItemRequest(anyLong(), anyLong());
    }
}