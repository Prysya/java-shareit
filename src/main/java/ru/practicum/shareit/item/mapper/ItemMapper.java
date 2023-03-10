package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

import java.util.Objects;

@Component
public class ItemMapper {


    public ItemResponseDto toResponseDto(Item item, UserDTO userDTO) {
        return ItemResponseDto.builder()
            .id(item.getId())
            .name(item.getName())
            .description(item.getDescription())
            .available(item.getAvailable())
            .owner(userDTO)
            .requestId(Objects.isNull(item.getRequest()) ? null : item.getRequest().getId())
            .build();
    }

    public Item toItem(ItemRequestDto itemRequestDto, User owner) {
        return Item.builder()
            .description(itemRequestDto.getDescription())
            .available(itemRequestDto.getAvailable())
            .name(itemRequestDto.getName())
            .owner(owner)
            .build();
    }
}
