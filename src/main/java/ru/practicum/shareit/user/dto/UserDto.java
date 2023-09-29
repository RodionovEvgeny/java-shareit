package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class UserDto {
    private long id;
    @NotBlank
    @Size(min = 0, max = 50)
    private String name;
    @NotBlank
    @Email(message = "Введен некорректный email.")
    private String email;
}
