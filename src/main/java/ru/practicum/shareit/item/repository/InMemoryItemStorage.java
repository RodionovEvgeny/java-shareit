package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private int vacantId = 1;

    @Override
    public Item addItem(Item item) {
        item.setId(vacantId++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItemById(long itemId) {
        if (items.containsKey(itemId)) {
            return items.get(itemId);
        } else {
            throw new EntityNotFoundException(
                    String.format("Предмет с id = %s не найден!", itemId),
                    Item.class.getName());
        }
    }

    @Override
    public List<Item> getOwnersItems(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findItems(String text) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item ->
                        item.getName().toLowerCase().contains(text) ||
                                item.getDescription().toLowerCase().contains(text))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItemsByOwnersId(long userId) {
        List<Long> itemsToDelete = items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .map(Item::getId)
                .collect(Collectors.toList());
        for (Long id : itemsToDelete) {
            items.remove(id);
        }
    }
}