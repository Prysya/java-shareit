package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {
    List<ItemResponseDto> getAllItems(Long userId, PageRequest pageRequest);

    ItemResponseDto saveItem(Long userId, ItemRequestDto itemDTO);

    ItemResponseDto getItemById(Long itemId, Long requestUserId);

    ItemResponseDto updateItem(Long itemId, Long userId, ItemRequestDto itemDTO);

    void deleteItem(Long userId, Long itemId);

    List<ItemResponseDto> searchAvailableItemsByText(Long userId, String text, PageRequest pageRequest);

    CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto);
}