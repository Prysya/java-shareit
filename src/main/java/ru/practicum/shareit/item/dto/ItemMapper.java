package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    public ItemDTO toDto(Item item) {
        return ItemDTO.builder()
            .id(item.getId())
            .name(item.getName())
            .description(item.getDescription())
            .available(item.getAvailable())
            .request(item.getRequest() != null ? item.getRequest().getId() : null)
            .build();
    }

    public Item toItem(ItemDTO itemDTO) {
        return Item.builder()
            .id(itemDTO.getId())
            .name(itemDTO.getName())
            .description(itemDTO.getDescription())
            .available(itemDTO.getAvailable())
            .build();
    }
}
