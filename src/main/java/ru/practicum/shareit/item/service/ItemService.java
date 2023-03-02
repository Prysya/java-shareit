package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.user.dto.UserDTO;

import java.util.List;

public interface ItemService {
    List<ItemDTO> getAllItems(UserDTO userDTO);

    ItemDTO saveItem(UserDTO userDTO, ItemDTO itemDTO);

    ItemDTO getItemById(Long itemId, Long requestUserId);

    ItemDTO updateItem(Long itemId, UserDTO userDTO, ItemDTO itemDTO);

    void deleteItem(UserDTO userDTO, Long itemId);

    List<ItemDTO> searchAvailableItemsByText(String text);

    CommentResponseDto addComment(UserDTO userDTO, Long itemId, CommentRequestDto commentRequestDto);
}