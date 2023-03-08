package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDTO;

import java.util.List;

public interface ItemService {
    List<ItemDTO> getAllItems(Long userId);

    ItemDTO saveItem(Long userId, ItemDTO itemDTO);

    ItemDTO getItemById(Long itemId, Long requestUserId);

    ItemDTO updateItem(Long itemId, Long userId, ItemDTO itemDTO);

    void deleteItem(Long userId, Long itemId);

    List<ItemDTO> searchAvailableItemsByText(String text);

    CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto);
}