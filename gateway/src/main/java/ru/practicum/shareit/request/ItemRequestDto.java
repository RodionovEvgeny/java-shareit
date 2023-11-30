package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
public class ItemRequestDto {
    private long id;
    @NotBlank(message = "Описание не должно быть пустым.")
    private String description;
    private LocalDateTime created;
}
