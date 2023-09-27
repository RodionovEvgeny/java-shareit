package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class UserDto {
    private long id;
    @NotEmpty(message = "Введено пустое имя.")
    private String name;
    @NotEmpty(message = "Введен пустой email.")
    @Email(message = "Введен некорректный email.")
    private String email;
}
