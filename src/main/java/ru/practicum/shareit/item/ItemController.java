package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;


    @GetMapping
    public List<ItemDTO> getAllItems(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.getAllItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDTO getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ItemDTO saveItem(
        @RequestHeader(USER_ID_HEADER) long userId,
        @Validated(ItemDTO.New.class) @RequestBody ItemDTO itemDTO
    ) {
        return itemService.saveItem(userId, itemDTO);
    }

    @PatchMapping("/{itemId}")
    public ItemDTO updateItem(
        @RequestHeader(USER_ID_HEADER) long userId,
        @Validated(ItemDTO.Update.class) @RequestBody ItemDTO itemDTO,
        @PathVariable Long itemId
    ) {
        return itemService.updateItem(itemId, userId, itemDTO);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable Long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDTO> searchAvailableItems(@RequestParam String text) {
        return itemService.searchAvailableItemsByText(text);
    }


}




