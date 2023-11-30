package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
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

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;

    private final BookingDto bookingRequest = BookingDto.builder()
            .start(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
            .end(LocalDateTime.of(2025, 1, 2, 0, 0, 0))
            .itemId(1L)
            .build();

    private final BookingDto bookingResponse = BookingDto.builder()
            .id(1L)
            .start(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
            .end(LocalDateTime.of(2025, 1, 2, 0, 0, 0))
            .itemId(1L)
            .status(BookingStatus.WAITING)
            .build();


    @Test
    void hasToAddBooking() throws Exception {
        when(bookingService.addBooking(anyLong(), any(BookingDto.class))).thenReturn(bookingResponse);
        mockMvc.perform(
                        post("/bookings")
                                .content(objectMapper.writeValueAsString(bookingRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(bookingResponse)));
        verify(bookingService, times(1)).addBooking(anyLong(), any(BookingDto.class));
    }

    @Test
    void hasToApproveBooking() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingResponse);
        mockMvc.perform(
                        patch("/bookings/1")
                                .param("approved", "TRUE")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(bookingResponse)));
        verify(bookingService, times(1)).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void hasToGetBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingResponse);
        mockMvc.perform(
                        get("/bookings/1")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(bookingResponse)));
        verify(bookingService, times(1)).getBookingById(anyLong(), anyLong());
    }

    @Test
    void hasToGetBookersBookings() throws Exception {
        when(bookingService.getBookersBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingResponse));
        mockMvc.perform(
                        get("/bookings")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(bookingResponse))));
        verify(bookingService, times(1)).getBookersBookings(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void hasToGetOwnersBookings() throws Exception {
        when(bookingService.getOwnersBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingResponse));
        mockMvc.perform(
                        get("/bookings/owner")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(bookingResponse))));
        verify(bookingService, times(1)).getOwnersBookings(anyLong(), anyString(), anyInt(), anyInt());
    }
}