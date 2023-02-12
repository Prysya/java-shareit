package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<Item> getAllItems(Long userId);

    Item saveItem(Long userId, Item item);

    Optional<Item> getItemById(Long id);

    Item updateItem(Long itemId, Item item);

    boolean deleteItem(Long itemId);

    List<Item> searchAvailableItemsByText(String text);
}