package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.common.AppPageRequest;
import ru.practicum.shareit.constant.AppErrorMessage;
import ru.practicum.shareit.constant.CustomHeaders;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getAllItems(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestParam(defaultValue = "0")
        @PositiveOrZero(message = AppErrorMessage.PAGE_IS_NOT_POSITIVE)
        Integer from,
        @RequestParam(defaultValue = "10")
        @Positive(message = AppErrorMessage.SIZE_IS_NOT_POSITIVE)
        Integer size
    ) {
        return ResponseEntity.ok(itemService.getAllItems(userId, new AppPageRequest(from, size)));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> getItemById(
        @PathVariable Long itemId,
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId
    ) {
        return ResponseEntity.ok(itemService.getItemById(itemId, userId));
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<ItemResponseDto> saveItem(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @Validated(ItemRequestDto.New.class) @RequestBody ItemRequestDto itemRequestDto
    ) {
        return new ResponseEntity<>(itemService.saveItem(userId, itemRequestDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> updateItem(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @Validated(ItemRequestDto.Update.class) @RequestBody ItemRequestDto itemRequestDto,
        @PathVariable Long itemId
    ) {
        return ResponseEntity.ok(itemService.updateItem(itemId, userId, itemRequestDto));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @PathVariable Long itemId
    ) {
        itemService.deleteItem(userId, itemId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemResponseDto>> searchAvailableItems(
        @RequestParam String text,
        @RequestParam(defaultValue = "0")
        @PositiveOrZero(message = AppErrorMessage.PAGE_IS_NOT_POSITIVE)
        Integer from,
        @RequestParam(defaultValue = "10")
        @Positive(message = AppErrorMessage.SIZE_IS_NOT_POSITIVE)
        Integer size
    ) {
        return ResponseEntity.ok(itemService.searchAvailableItemsByText(text, new AppPageRequest(from, size)));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> addComment(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @PathVariable long itemId,
        @Valid @RequestBody CommentRequestDto commentRequestDto
    ) {
        return ResponseEntity.ok(itemService.addComment(userId, itemId, commentRequestDto));
    }
}




