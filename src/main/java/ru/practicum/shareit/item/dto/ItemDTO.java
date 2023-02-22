package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

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
    @Null
    private UserDTO owner;
    @Null
    private Long request;

    public interface New {
    }

    public interface Update {
    }
}
