package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class ItemDto {
    private long id;
    @NotBlank
    @Size(min = 0, max = 50)
    private String name;
    @NotBlank
    @Size(min = 0, max = 300)
    private String description;
    @NotNull
    private Boolean available;
    private ItemRequest request;
}
