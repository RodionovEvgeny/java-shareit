package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {
    private final ItemDto requestItem = ItemDto.builder()
            .name("Name")
            .description("Description")
            .available(Boolean.TRUE)
            .build();
    private final ItemDto responseItem = ItemDto.builder()
            .id(1L)
            .name("Name")
            .description("Description")
            .available(Boolean.TRUE)
            .build();
    private final CommentDto requestComment = CommentDto.builder()
            .text("Comment")
            .build();
    private final CommentDto responseComment = CommentDto.builder()
            .id(1L)
            .text("Comment")
            .authorName("Author Name")
            .created(LocalDateTime.now())
            .build();

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;

    @Test
    void hasToAddItem() throws Exception {
        when(itemService.addItem(anyLong(), any(ItemDto.class))).thenReturn(responseItem);
        mockMvc.perform(
                        post("/items")
                                .content(objectMapper.writeValueAsString(requestItem))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(responseItem)));
        verify(itemService, times(1)).addItem(anyLong(), any(ItemDto.class));
    }

    @Test
    void hasToUpdateItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(responseItem);
        mockMvc.perform(
                        patch("/items/1")
                                .content(objectMapper.writeValueAsString(requestItem))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(responseItem)));
        verify(itemService, times(1)).updateItem(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    void hasToGetItem() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(responseItem);
        mockMvc.perform(
                        get("/items/1")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(responseItem)));
        verify(itemService, times(1)).getItemById(anyLong(), anyLong());
    }

    @Test
    void hasToGetOwnersItems() throws Exception {
        when(itemService.getOwnersItems(anyLong(), anyInt(), anyInt())).thenReturn(List.of(responseItem));
        mockMvc.perform(
                        get("/items")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(responseItem))));
        verify(itemService, times(1)).getOwnersItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    void hasToFindItems() throws Exception {
        when(itemService.findItems(anyString(), anyInt(), anyInt())).thenReturn(List.of(responseItem));
        mockMvc.perform(
                        get("/items/search")
                                .param("text", "query")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(responseItem))));
        verify(itemService, times(1)).findItems(anyString(), anyInt(), anyInt());

    }

    @Test
    void hasToAddComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(responseComment);
        mockMvc.perform(
                        post("/items/1//comment")
                                .content(objectMapper.writeValueAsString(requestComment))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(responseComment)));
        verify(itemService, times(1)).addComment(anyLong(), anyLong(), any(CommentDto.class));
    }
}