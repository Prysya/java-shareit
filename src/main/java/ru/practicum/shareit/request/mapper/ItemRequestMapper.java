package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDTO;

import java.util.List;


@UtilityClass
public class ItemRequestMapper {
    public static ItemRequestResponseDto toResponseDto(
        ItemRequest itemRequest, UserDTO requestor, List<ItemResponseDto> items
    ) {
        return ItemRequestResponseDto.builder()
            .id(itemRequest.getId())
            .description(itemRequest.getDescription())
            .created(itemRequest.getCreated())
            .requestor(requestor)
            .items(items)
            .build();
    }

    public static ItemRequest toItemRequest(ItemRequestRequestDto itemRequestRequestDto) {
        return ItemRequest.builder()
            .description(itemRequestRequestDto.getDescription())
            .build();
    }
}
