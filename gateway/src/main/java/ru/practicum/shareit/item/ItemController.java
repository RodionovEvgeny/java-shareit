package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable(name = "itemId") long itemId,
                                             @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable(name = "itemId") long itemId) {
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnersItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "from", defaultValue = "0")
            @Min(value = 0, message = "Номер запроса с которого начнется страница должен быть больше 0") int from,
            @RequestParam(value = "size", defaultValue = "10")
            @Min(value = 1, message = "Размер страницы должен быть больше 0") int size) {
        return itemClient.getOwnersItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "text") String text,
            @RequestParam(value = "from", defaultValue = "0")
            @Min(value = 0, message = "Номер запроса с которого начнется страница должен быть больше 0") int from,
            @RequestParam(value = "size", defaultValue = "10")
            @Min(value = 1, message = "Размер страницы должен быть больше 0") int size) {
        return itemClient.findItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable(name = "itemId") long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
