package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    Map<Long, Item> items = new HashMap<>();
    Map<Long, List<Long>> ownerItems = new HashMap<>();

    @Override
    public List<Item> getAllItems(Long userId) {
        return ownerItems.getOrDefault(userId, new ArrayList<>()).stream()
            .map(itemId -> items.get(itemId))
            .filter(Objects::nonNull)
            .collect(
                Collectors.toList());
    }

    @Override
    public Item saveItem(Long userId, Item item) {
        Long itemId = Integer.toUnsignedLong(items.size()) + 1;
        item.setId(itemId);
        item.setOwner(userId);

        items.put(itemId, item);
        ownerItems.merge(userId, new ArrayList<>(List.of(itemId)), (oldList, newList) -> {
            oldList.addAll(newList);
            return oldList;
        });

        return item;
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item updateItem(Long itemId, Item item) {
        Item oldItem = items.get(itemId);

        if (Objects.isNull(oldItem)) {
            return null;
        }

        Item newItem = Item.builder()
            .id(oldItem.getId())
            .owner(oldItem.getOwner())
            .request(oldItem.getRequest())
            .name(Objects.requireNonNullElse(item.getName(), oldItem.getName()))
            .description(Objects.requireNonNullElse(item.getDescription(), oldItem.getDescription()))
            .available(Objects.requireNonNullElse(item.getAvailable(), oldItem.getAvailable()))
            .build();

        items.put(itemId, newItem);
        return newItem;
    }

    @Override
    public boolean deleteItem(Long itemId) {
        if (!items.containsKey(itemId)) {
            return false;
        }

        items.put(itemId, null);

        return true;
    }

    @Override
    public List<Item> searchAvailableItemsByText(String text) {
        return items.values().stream().filter(Item::getAvailable).filter(item ->
            item.getName().toLowerCase().contains(text.toLowerCase())
                || item.getDescription().toLowerCase().contains(text.toLowerCase())
        ).collect(Collectors.toList());
    }
}