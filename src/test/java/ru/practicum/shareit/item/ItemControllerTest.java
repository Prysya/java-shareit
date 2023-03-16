package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.common.AppPageRequest;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    private static final int DEFAULT_FROM = 0;
    private static final int DEFAULT_SIZE = 10;
    private final AppPageRequest defaultAppPageRequest = new AppPageRequest(DEFAULT_FROM, DEFAULT_SIZE);
    @InjectMocks
    private ItemController itemController;
    @Mock
    private ItemService itemService;

    @Test
    void getAllItems_whenInvoked_thenResponseStatusOkAndItemsListInBody() {
        long userId = 1L;
        ItemResponseDto itemResponseDto = ItemResponseDto.builder().build();
        List<ItemResponseDto> bookingList = List.of(itemResponseDto);

        when(itemService.getAllItems(anyLong(), any(PageRequest.class)))
            .thenReturn(bookingList);

        ResponseEntity<List<ItemResponseDto>> response = itemController.getAllItems(userId, DEFAULT_FROM, DEFAULT_SIZE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookingList, response.getBody());
    }

    @Test
    void getItemById_whenInvoked_thenResponseStatusOkAndItemInBody() {
        long userId = 1L;
        long itemId = 1L;
        ItemResponseDto itemResponseDto = ItemResponseDto.builder().build();

        when(itemService.getItemById(anyLong(), anyLong()))
            .thenReturn(itemResponseDto);

        ResponseEntity<ItemResponseDto> response = itemController.getItemById(itemId, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(itemResponseDto, response.getBody());
    }

    @Test
    void saveItem_whenInvoked_thenResponseStatusCreatedAndItemInBody() {
        long itemId = 1L;
        ItemResponseDto itemResponseDto = ItemResponseDto.builder().build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().build();

        when(itemService.saveItem(anyLong(), any(ItemRequestDto.class)))
            .thenReturn(itemResponseDto);

        ResponseEntity<ItemResponseDto> response = itemController.saveItem(itemId, itemRequestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(itemResponseDto, response.getBody());
    }

    @Test
    void updateItem_whenInvoked_thenResponseStatusOkAndItemInBody() {
        long itemId = 1L;
        long userId = 1L;
        ItemResponseDto itemResponseDto = ItemResponseDto.builder().build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().build();

        when(itemService.updateItem(anyLong(), anyLong(), any(ItemRequestDto.class)))
            .thenReturn(itemResponseDto);

        ResponseEntity<ItemResponseDto> response = itemController.updateItem(itemId, itemRequestDto, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(itemResponseDto, response.getBody());
    }

    @Test
    void deleteItem_whenInvoked_thenResponseStatusOk() {
        long itemId = 1L;
        long userId = 1L;

        ResponseEntity<Void> response = itemController.deleteItem(itemId, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void searchAvailableItems_whenInvoked_thenResponseStatusOkAndItemsListInBody() {
        String text = "text";
        ItemResponseDto itemResponseDto = ItemResponseDto.builder().build();
        List<ItemResponseDto> list = List.of(itemResponseDto);

        when(itemService.searchAvailableItemsByText(anyString(), any(AppPageRequest.class)))
            .thenReturn(list);

        ResponseEntity<List<ItemResponseDto>> response =
            itemController.searchAvailableItems(text, DEFAULT_FROM, DEFAULT_SIZE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(list, response.getBody());
    }

    @Test
    void addComment_whenInvoked_thenResponseStatusOkAndCommentInBody() {
        long userId = 1L;
        long itemId = 1L;
        CommentRequestDto commentRequestDto = CommentRequestDto.builder().build();
        CommentResponseDto commentResponseDto = CommentResponseDto.builder().build();

        when(itemService.addComment(anyLong(), anyLong(), any(CommentRequestDto.class)))
            .thenReturn(commentResponseDto);

        ResponseEntity<CommentResponseDto> response =
            itemController.addComment(userId, itemId, commentRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(commentResponseDto, response.getBody());
    }
}