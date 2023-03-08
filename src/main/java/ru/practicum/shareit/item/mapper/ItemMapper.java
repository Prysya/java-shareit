package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

@Component
public class ItemMapper {

    public ItemDTO toDto(Item item, UserDTO userDTO) {
        return ItemDTO.builder()
            .id(item.getId())
            .name(item.getName())
            .description(item.getDescription())
            .available(item.getAvailable())
            .owner(userDTO)
            .build();
    }

    public Item toItem(ItemDTO itemDTO, User user) {
        return Item.builder()
            .id(itemDTO.getId())
            .name(itemDTO.getName())
            .description(itemDTO.getDescription())
            .available(itemDTO.getAvailable())
            .owner(user)
            .build();
    }
}
