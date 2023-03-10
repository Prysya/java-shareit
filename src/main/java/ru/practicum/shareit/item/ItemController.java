package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.constant.CustomHeaders;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDTO> getAllItems(@RequestHeader(CustomHeaders.USER_ID_HEADER) long userId) {
        return itemService.getAllItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDTO getItemById(
        @PathVariable Long itemId,
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId
    ) {
        return itemService.getItemById(itemId, userId);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ItemDTO saveItem(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @Validated(ItemDTO.New.class) @RequestBody ItemDTO itemDTO
    ) {
        return itemService.saveItem(userId, itemDTO);
    }

    @PatchMapping("/{itemId}")
    public ItemDTO updateItem(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @Validated(ItemDTO.Update.class) @RequestBody ItemDTO itemDTO,
        @PathVariable Long itemId
    ) {
        return itemService.updateItem(itemId, userId, itemDTO);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(CustomHeaders.USER_ID_HEADER) long userId, @PathVariable Long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDTO> searchAvailableItems(@RequestParam String text) {
        return itemService.searchAvailableItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @PathVariable long itemId,
        @Valid @RequestBody CommentRequestDto commentRequestDto
    ) {
        return itemService.addComment(userId, itemId, commentRequestDto);
    }
}




