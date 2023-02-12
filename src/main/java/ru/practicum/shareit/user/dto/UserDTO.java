package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.utils.RegexPatterns;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    @Null
    private Long id;
    @NotBlank(groups = {New.class})
    @Size(groups = {Update.class, New.class}, min = 1)
    private String name;
    @NotBlank(groups = {New.class})
    @Size(groups = {Update.class, New.class}, min = 1)
    @Email(regexp = RegexPatterns.email, groups = {New.class, Update.class})
    private String email;
    public interface New {
    }
    public interface Update {
    }
}
