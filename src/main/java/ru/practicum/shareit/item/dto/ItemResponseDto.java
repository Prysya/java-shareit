package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingItemResponseDTO;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.user.dto.UserDTO;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private UserDTO owner;
    private BookingItemResponseDTO lastBooking;
    private BookingItemResponseDTO nextBooking;
    private List<CommentResponseDto> comments;
    private Long requestId;
}