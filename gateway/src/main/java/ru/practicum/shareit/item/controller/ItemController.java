package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.constant.AppErrorMessage;
import ru.practicum.shareit.constant.CustomHeaders;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllItems(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestParam(defaultValue = "0")
        @PositiveOrZero(message = AppErrorMessage.PAGE_IS_NOT_POSITIVE)
        Integer from,
        @RequestParam(defaultValue = "10")
        @Positive(message = AppErrorMessage.SIZE_IS_NOT_POSITIVE)
        Integer size
    ) {
        log.info("Get all items with userId={}, from={}, size={}", userId, from, size);
        return itemClient.getAllItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @PathVariable Long itemId
    ) {
        log.info("Get item by id with userId={}, itemId={}", userId, itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> saveItem(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @Validated(ItemRequestDto.New.class) @RequestBody ItemRequestDto itemRequestDto
    ) {
        log.info("Save item with userId={}, item={}", userId, itemRequestDto);
        return itemClient.saveItem(userId, itemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @PathVariable Long itemId,
        @Validated(ItemRequestDto.Update.class) @RequestBody ItemRequestDto itemRequestDto
    ) {
        log.info("Update item with userId={}, itemId={}, item={}", userId, itemId, itemRequestDto);
        return itemClient.updateItem(userId, itemId, itemRequestDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @PathVariable Long itemId
    ) {
        log.info("Delete item with userId={}, itemId={}", userId, itemId);
        return itemClient.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchAvailableItemsByText(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestParam String text,
        @RequestParam(defaultValue = "0")
        @PositiveOrZero(message = AppErrorMessage.PAGE_IS_NOT_POSITIVE)
        Integer from,
        @RequestParam(defaultValue = "10")
        @Positive(message = AppErrorMessage.SIZE_IS_NOT_POSITIVE)
        Integer size
    ) {
        log.info("Search available items item with userId={}, text={}, from={}, size={}", userId, text, from, size);
        return itemClient.searchAvailableItemsByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @PathVariable long itemId,
        @Valid @RequestBody CommentRequestDto commentRequestDto
    ) {
        log.info("Add comment with userId={}, itemId={}, comment={}", userId, itemId, commentRequestDto);
        return itemClient.addComment(userId, itemId, commentRequestDto);
    }
}
