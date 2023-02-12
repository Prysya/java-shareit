package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
class ItemServiceImpl implements ItemService {
    public static final String NOT_FOUND = "Вещь c id: '%d' не найдена";
    public static final String UNAUTHORIZED = "Вещь с id: '%d', не принадлежит пользователю с id: '%d";

    private final ItemRepository repository;

    @Override
    public List<Item> getAllItems(Long userId) {
        return repository.getAllItems(userId);
    }

    @Override
    public Item saveItem(Long userId, Item item) {
        return repository.saveItem(userId, item);
    }

    @Override
    public Item getItemById(Long itemId) {
        return repository.getItemById(itemId)
            .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND, itemId)));
    }

    @Override
    public Item updateItem(Long itemId, Long userId, Item item) {
        checkAuth(userId, getItemById(itemId));

        return repository.updateItem(itemId, item);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        checkAuth(userId, getItemById(itemId));

        if (!repository.deleteItem(itemId)) {
            throw new NotFoundException(String.format(NOT_FOUND, itemId));
        }
    }

    @Override
    public List<Item> searchAvailableItemsByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return repository.searchAvailableItemsByText(text);
    }

    private void checkAuth(Long userId, Item item) {
        if (!Objects.equals(item.getOwner(), userId)) {
            throw new NotFoundException(String.format(UNAUTHORIZED, item.getId(), userId));
        }
    }
}