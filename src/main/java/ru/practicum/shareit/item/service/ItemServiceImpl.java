package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        validateUserById(userId);
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, userId));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        validateUserById(userId);
        Item updatedItem = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Предмет с id = %s не найден!", itemId),
                Item.class.getName()));
        if (updatedItem.getOwner() != userId)
            throw new NoAccessException("Редактировать предмет может только его хозяин!");
        if (itemDto.getName() != null) updatedItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null) updatedItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) updatedItem.setAvailable(itemDto.getAvailable());
        return ItemMapper.toItemDto(itemRepository.save(updatedItem));
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return ItemMapper.toItemDto(itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Предмет с id = %s не найден!", itemId),
                Item.class.getName())));
    }

    @Override
    public List<ItemDto> getOwnersItems(long userId) {
        validateUserById(userId);
        return ItemMapper.toItemDtoList(itemRepository.findByOwner(userId));
    }

    @Override
    public List<ItemDto> findItems(String text) {
        if (text == null || text.isBlank()) return new ArrayList<>();
        return ItemMapper.toItemDtoList(itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text));
    }

    private void validateUserById(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Пользователь с id = %s не найден!", userId),
                User.class.getName()));
    }
}
