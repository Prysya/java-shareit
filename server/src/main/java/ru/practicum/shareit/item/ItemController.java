package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.common.AppPageRequest;
import ru.practicum.shareit.constant.CustomHeaders;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemResponseDto> getAllItems(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestParam Integer from,
        @RequestParam Integer size
    ) {
        return itemService.getAllItems(userId, new AppPageRequest(from, size));
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(
        @PathVariable Long itemId,
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId
    ) {
        return itemService.getItemById(itemId, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto saveItem(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestBody ItemRequestDto itemRequestDto
    ) {
        return itemService.saveItem(userId, itemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestBody ItemRequestDto itemRequestDto,
        @PathVariable Long itemId
    ) {
        return itemService.updateItem(itemId, userId, itemRequestDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @PathVariable Long itemId
    ) {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchAvailableItems(
        @RequestParam String text,
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestParam Integer from,
        @RequestParam Integer size
    ) {
        return itemService.searchAvailableItemsByText(userId, text, new AppPageRequest(from, size));
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @PathVariable long itemId,
        @RequestBody CommentRequestDto commentRequestDto
    ) {
        return itemService.addComment(userId, itemId, commentRequestDto);
    }
}




