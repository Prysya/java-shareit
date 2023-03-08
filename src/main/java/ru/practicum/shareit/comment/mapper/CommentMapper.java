package ru.practicum.shareit.comment.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.user.dto.UserDTO;

@Component
public class CommentMapper {
    public CommentResponseDto toResponseDto(Comment comment, UserDTO authorDTO) {
        return CommentResponseDto.builder()
            .id(comment.getId())
            .created(comment.getCreated())
            .text(comment.getText())
            .authorName(authorDTO.getName())
            .build();
    }

    public Comment toComment(CommentRequestDto commentRequestDto) {
        return Comment.builder()
            .text(commentRequestDto.getText())
            .build();
    }
}
