package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class BookingDtoTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testDateDeserialization() throws JsonProcessingException {
        String json = "{\n" +
                "    \"itemId\": 1,\n" +
                "    \"start\": \"2023-11-11T11:11:11\",\n" +
                "    \"end\": \"2023-12-12T12:12:12\"\n" +
                "} ";

        BookingDto bookingDto = objectMapper.readValue(json, BookingDto.class);

        assertEquals(1L, bookingDto.getItemId());
        assertEquals(LocalDateTime.of(2023, 11, 11, 11, 11, 11),
                bookingDto.getStart());
        assertEquals(LocalDateTime.of(2023, 12, 12, 12, 12, 12),
                bookingDto.getEnd());
    }

    @Test
    public void testDateSerialization() throws JsonProcessingException {
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 11, 11, 11, 11, 11))
                .end(LocalDateTime.of(2023, 12, 12, 12, 12, 12))
                .build();
        String expectedJson = "{" +
                "\"id\":1," +
                "\"start\":\"2023-11-11T11:11:11\"," +
                "\"end\":\"2023-12-12T12:12:12\"," +
                "\"itemId\":null," +
                "\"item\":null," +
                "\"bookerId\":null," +
                "\"booker\":null," +
                "\"status\":null" +
                "}";
        String json = objectMapper.writeValueAsString(bookingDto);
        assertEquals(expectedJson, json);
    }
}