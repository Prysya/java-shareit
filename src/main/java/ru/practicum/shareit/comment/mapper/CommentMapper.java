package ru.practicum.shareit.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.user.dto.UserDTO;


@UtilityClass
public class CommentMapper {
    public static CommentResponseDto toResponseDto(Comment comment, UserDTO authorDTO) {
        return CommentResponseDto.builder()
            .id(comment.getId())
            .created(comment.getCreated())
            .text(comment.getText())
            .authorName(authorDTO.getName())
            .build();
    }

    public static Comment toComment(CommentRequestDto commentRequestDto) {
        return Comment.builder()
            .text(commentRequestDto.getText())
            .build();
    }
}
