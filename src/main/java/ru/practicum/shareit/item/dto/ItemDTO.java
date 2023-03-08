package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingItemResponseDTO;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.user.dto.UserDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {

    private Long id;

    @NotBlank(groups = {New.class})
    @Size(groups = {Update.class, New.class}, min = 1)
    private String name;

    @NotBlank(groups = {New.class})
    @Size(groups = {Update.class, New.class}, min = 1)
    private String description;

    @NotNull(groups = {New.class})
    private Boolean available;
    private UserDTO owner;
    private Long request;
    private BookingItemResponseDTO lastBooking;
    private BookingItemResponseDTO nextBooking;
    private List<CommentResponseDto> comments;

    public interface New {
    }

    public interface Update {
    }
}
