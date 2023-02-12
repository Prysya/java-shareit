package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getAllItems(Long userId);

    Item saveItem(Long userId, Item item);

    Item getItemById(Long itemId);

    Item updateItem(Long itemId, Long userId, Item item);

    void deleteItem(Long userId, Long itemId);

    List<Item> searchAvailableItemsByText(String text);
}