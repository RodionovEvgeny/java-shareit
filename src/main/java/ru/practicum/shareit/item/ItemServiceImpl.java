package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        itemDto.setOwner(userId);
        Item item = itemStorage.addItem(ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item newItem = itemStorage.getItem(itemId);
        if (itemDto.getName() != null) newItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null) newItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) newItem.setAvailable(itemDto.getAvailable());

        return ItemMapper.toItemDto(itemStorage.updateItem(itemId, newItem));
    }

    @Override
    public ItemDto getItem(long itemId) {
        Item item = itemStorage.getItem(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getOwnersItems(long userId) {
        return ItemMapper.toItemDto(itemStorage.getOwnersItems(userId));
    }

    @Override
    public List<ItemDto> findItems(String text) {
        return ItemMapper.toItemDto(itemStorage.findItems(text));
    }
}
