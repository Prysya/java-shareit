package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDTO;

import java.util.List;

@Component
public class ItemRequestMapper {
    public ItemRequestResponseDto toResponseDto(
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

    public ItemRequest toItemRequest(ItemRequestRequestDto itemRequestRequestDto) {
        return ItemRequest.builder()
            .description(itemRequestRequestDto.getDescription())
            .build();
    }
}
