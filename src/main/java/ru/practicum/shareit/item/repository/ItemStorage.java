package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemStorage {

    Item addItem(Item item);

    Item updateItem(long itemId, Item item);

    Item getItemById(long itemId);

    List<Item> getOwnersItems(long userId);

    List<Item> findItems(String text);

    void deleteItemsByOwnersId(long userId);
}
