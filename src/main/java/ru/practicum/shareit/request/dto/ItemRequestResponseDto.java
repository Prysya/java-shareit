package ru.practicum.shareit.request.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestResponseDto {
    private Long id;
    private LocalDateTime created;
    private String description;
    private UserDTO requestor;
    private List<ItemResponseDto> items;
}
