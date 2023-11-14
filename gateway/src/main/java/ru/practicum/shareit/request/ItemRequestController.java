package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnersItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getOwnersItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "from", defaultValue = "0")
            @Min(value = 0, message = "Номер запроса с которого начнется страница должен быть больше 0") int from,
            @RequestParam(value = "size", defaultValue = "10")
            @Min(value = 1, message = "Размер страницы должен быть больше 0") int size) {
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable(name = "requestId") long requestId) {
        return itemRequestClient.getItemRequest(userId, requestId);
    }
}
