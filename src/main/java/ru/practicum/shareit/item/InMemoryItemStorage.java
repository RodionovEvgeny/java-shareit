package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
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
    public Item updateItem(long itemId, Item item) {
        item.setId(itemId);
        items.put(itemId, item);
        return item;
    }

    @Override
    public Item getItem(long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getOwnersItems(long userId) {
        return items.values().stream().filter(item -> item.getOwner() == userId).collect(Collectors.toList());
    }

    @Override
    public List<Item> findItems(String text) {
        return items.values().stream()
                .filter(item -> item.getName().contains(text) || item.getDescription().contains(text))
                .collect(Collectors.toList());
    }
}
