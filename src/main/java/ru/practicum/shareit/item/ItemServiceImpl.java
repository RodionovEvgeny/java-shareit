package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        userStorage.getUserById(userId);
        itemDto.setOwner(userId);
        Item item = itemStorage.addItem(ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        userStorage.getUserById(userId);
        Item newItem = itemStorage.getItemById(itemId);
        if (newItem.getOwner() != userId) throw new NoAccessException("Редактировать предмет может только его хозяин!");
        if (itemDto.getName() != null) newItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null) newItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) newItem.setAvailable(itemDto.getAvailable());
        return ItemMapper.toItemDto(itemStorage.updateItem(itemId, newItem));
    }

    @Override
    public ItemDto getItemById(long itemId) {
        Item item = itemStorage.getItemById(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getOwnersItems(long userId) {
        userStorage.getUserById(userId);
        return ItemMapper.toItemDto(itemStorage.getOwnersItems(userId));
    }

    @Override
    public List<ItemDto> findItems(String text) {
        if (text == null || text.isBlank()) return new ArrayList<>();
        return ItemMapper.toItemDto(itemStorage.findItems(text.toLowerCase()));
    }
}
