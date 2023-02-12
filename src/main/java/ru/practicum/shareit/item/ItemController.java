package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final UserMapper userMapper;


    @GetMapping
    public List<ItemDTO> getAllItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        userService.getUserById(userId);

        return itemService.getAllItems(userId).stream().map(item -> {
            ItemDTO itemDTO = itemMapper.toDto(item);
            setOwnerToDTO(itemDTO, item.getOwner());
            return itemDTO;
        }).collect(Collectors.toList());
    }

    @GetMapping("/{itemId}")
    public ItemDTO getItemById(@PathVariable Long itemId) {
        Item item = itemService.getItemById(itemId);
        ItemDTO itemDTO = itemMapper.toDto(item);

        setOwnerToDTO(itemDTO, item.getOwner());

        return itemDTO;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ItemDTO saveItem(
        @RequestHeader("X-Sharer-User-Id") long userId,
        @Validated(ItemDTO.New.class) @RequestBody ItemDTO itemDTO
    ) {
        userService.getUserById(userId);

        ItemDTO newItemDTO = itemMapper.toDto(itemService.saveItem(userId, itemMapper.toItem(itemDTO)));

        setOwnerToDTO(newItemDTO, userId);

        return newItemDTO;
    }

    @PatchMapping("/{itemId}")
    public ItemDTO updateItem(
        @RequestHeader("X-Sharer-User-Id") long userId,
        @Validated(ItemDTO.Update.class) @RequestBody ItemDTO itemDTO,
        @PathVariable Long itemId
    ) {
        userService.getUserById(userId);

        ItemDTO updatedItemDTO = itemMapper.toDto(itemService.updateItem(itemId, userId, itemMapper.toItem(itemDTO)));

        setOwnerToDTO(updatedItemDTO, userId);

        return updatedItemDTO;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long itemId) {
        userService.getUserById(userId);

        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDTO> searchAvailableItems(@RequestParam String text) {
        return itemService.searchAvailableItemsByText(text).stream().map(item -> {
            ItemDTO itemDTO = itemMapper.toDto(item);
            setOwnerToDTO(itemDTO, item.getOwner());
            return itemDTO;
        }).collect(Collectors.toList());
    }

    private void setOwnerToDTO(ItemDTO itemDTO, Long userId) {
        itemDTO.setOwner(userMapper.toDto(userService.getUserById(userId)));
    }
}




