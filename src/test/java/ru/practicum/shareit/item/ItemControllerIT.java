package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.common.AppPageRequest;
import ru.practicum.shareit.constant.CustomHeaders;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@ExtendWith(MockitoExtension.class)
class ItemControllerIT {
    private static final String CONTROLLER_URL = "/items";
    private static final Integer DEFAULT_FROM = 0;
    private static final Integer DEFAULT_SIZE = 10;
    private final AppPageRequest defaultAppPageRequest = new AppPageRequest(DEFAULT_FROM, DEFAULT_SIZE);
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;

    /**
     * getAllItems
     */
    @Test
    @SneakyThrows
    void getAllItems_whenUserIdHeaderIsPresentedAndPaginationWithDefaultValues_thenStatusIsOk() {
        long userId = 1L;

        mockMvc
            .perform(
                get(CONTROLLER_URL)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
            )
            .andExpect(status().isOk());

        verify(itemService).getAllItems(userId, defaultAppPageRequest);
    }

    @Test
    @SneakyThrows
    void getAllItems_whenUserIdHeaderIsPresentedAndSizeIsPresented_thenStatusIsOk() {
        long userId = 1L;
        int size = 20;

        mockMvc
            .perform(
                get(CONTROLLER_URL)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .param("size", String.valueOf(size))
            )
            .andExpect(status().isOk());

        verify(itemService).getAllItems(userId, new AppPageRequest(DEFAULT_FROM, size));
    }

    @Test
    @SneakyThrows
    void getAllItems_whenUserIdHeaderIsPresentedAndFromIsPresented_thenStatusIsOk() {
        long userId = 1L;
        int from = 2;

        mockMvc
            .perform(
                get(CONTROLLER_URL)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .param("from", String.valueOf(from))
            )
            .andExpect(status().isOk());

        verify(itemService).getAllItems(userId, new AppPageRequest(from, DEFAULT_SIZE));
    }

    @Test
    @SneakyThrows
    void getAllItems_whenUserIdHeaderIsNotPresented_thenReturnedBadRequest() {
        mockMvc
            .perform(
                get(CONTROLLER_URL)
            )
            .andExpect(status().isBadRequest());

        verify(itemService, never()).getAllItems(anyLong(), any(AppPageRequest.class));
    }

    @Test
    @SneakyThrows
    void getAllItems_whenFromHasNegativeValue_thenReturnedBadRequest() {
        long userId = 1L;
        int from = -1;

        mockMvc.perform(
                get(CONTROLLER_URL)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .param("from", String.valueOf(from))
            )
            .andExpect(status().isBadRequest());

        verify(itemService, never()).getAllItems(anyLong(), any(AppPageRequest.class));

    }

    @Test
    @SneakyThrows
    void getAllItems_whenSizeHasNegativeValue_thenReturnedBadRequest() {
        long userId = 1L;
        int size = -1;

        mockMvc.perform(
                get(CONTROLLER_URL)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .param("size", String.valueOf(size))
            )
            .andExpect(status().isBadRequest());

        verify(itemService, never()).getAllItems(anyLong(), any(AppPageRequest.class));
    }

    /**
     * getItemById
     */
    @Test
    @SneakyThrows
    void getItemById_whenUserIdHeaderIsPresentedAndBookingIdHasCorrectType_thenStatusIsOK() {
        long itemId = 1L;
        long userId = 1L;

        mockMvc
            .perform(
                get(CONTROLLER_URL + "/{itemId}", itemId)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
            )
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getItemById_whenUserIdHeaderIsNotPresented_thenReturnedBadRequest() {
        long itemId = 1L;
        long userId = 1L;

        mockMvc
            .perform(
                get(CONTROLLER_URL + "/{itemId}", itemId)
            )
            .andExpect(status().isBadRequest());

        verify(itemService, never()).getItemById(anyLong(), eq(userId));
    }

    @Test
    @SneakyThrows
    void getItemById_whenItemIdHasWrongType_thenReturnedBadRequest() {
        String itemId = "String";
        long userId = 1L;

        mockMvc
            .perform(
                get(CONTROLLER_URL + "/{itemId}", itemId)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
            )
            .andExpect(status().isBadRequest());

        verify(itemService, never()).getItemById(anyLong(), eq(userId));
    }

    /**
     * saveItem
     */
    @Test
    @SneakyThrows
    void saveItem_whenItemIsValidAndUserIdIsPresented_thenReturnedCreatedItemAndStatusIsCreated() {
        long userId = 1L;
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .name("name").description("desc").available(false).build();
        ItemResponseDto itemResponseDto = ItemResponseDto.builder().build();

        when(itemService.saveItem(eq(userId), any(ItemRequestDto.class))).thenReturn(itemResponseDto);

        String json = mockMvc
            .perform(
                post(CONTROLLER_URL)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(itemRequestDto))
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemResponseDto), json);
    }

    @Test
    @SneakyThrows
    void saveItem_whenItemIsNotValid_thenReturnedBadRequest() {
        long userId = 1L;

        mockMvc
            .perform(
                post(CONTROLLER_URL)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(ItemRequestDto.builder().build()))
            )
            .andExpect(status().isBadRequest());

        verify(itemService, never()).saveItem(eq(userId), any(ItemRequestDto.class));
    }

    /**
     * updateItem
     */
    @Test
    @SneakyThrows
    void updateItem_whenItemIsValidAndUserIdIsPresented_thenReturnedCreatedItemAndStatusIsOk() {
        long itemId = 1L;
        long userId = 1L;
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .name("name").description("desc").available(false).build();
        ItemResponseDto itemResponseDto = ItemResponseDto.builder().build();

        when(itemService.updateItem(eq(itemId), eq(userId), any(ItemRequestDto.class))).thenReturn(itemResponseDto);

        String json = mockMvc
            .perform(
                patch(CONTROLLER_URL + "/{itemId}", itemId)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(itemRequestDto))
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemResponseDto), json);
    }

    @Test
    @SneakyThrows
    void updateItem_whenUserIdIsNotPresented_thenReturnedBadRequest() {
        long itemId = 1L;
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .name("name").description("desc").available(false).build();

        mockMvc
            .perform(
                patch(CONTROLLER_URL + "/{itemId}", itemId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(itemRequestDto))
            )
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    /**
     * deleteItem
     */
    @Test
    @SneakyThrows
    void deleteItem_whenItemIsValidAndUserIdIsPresented_thenReturnedCreatedItemAndStatusIsOk() {
        long itemId = 1L;
        long userId = 1L;

        mockMvc
            .perform(
                delete(CONTROLLER_URL + "/{itemId}", itemId)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
            )
            .andExpect(status().isOk());

        verify(itemService).deleteItem(userId, itemId);
    }

    @Test
    @SneakyThrows
    void deleteItem_whenUserIdIsNotPresented_thenReturnedBadRequest() {
        long itemId = 1L;

        mockMvc
            .perform(
                delete(CONTROLLER_URL + "/{itemId}", itemId)
            )
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    /**
     * searchAvailableItems
     */
    @Test
    @SneakyThrows
    void searchAvailableItems_whenPaginationWithDefaultValues_thenStatusIsOk() {
        String text = "text";
        long userId = 1L;

        mockMvc
            .perform(
                get(CONTROLLER_URL + "/search")
                    .param("text", text)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
            )
            .andExpect(status().isOk());

        verify(itemService).searchAvailableItemsByText(eq(userId), eq(text), any(AppPageRequest.class));
    }

    @Test
    @SneakyThrows
    void searchAvailableItems_whenUSizeIsPresented_thenStatusIsOk() {
        long userId = 1L;
        int size = 20;
        String text = "text";

        mockMvc
            .perform(
                get(CONTROLLER_URL + "/search")
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .param("size", String.valueOf(size))
                    .param(text, text)
            )
            .andExpect(status().isOk());

        verify(itemService).searchAvailableItemsByText(userId, text, new AppPageRequest(DEFAULT_FROM, size));
    }

    @Test
    @SneakyThrows
    void searchAvailableItems_whenFromIsPresented_thenStatusIsOk() {
        long userId = 1L;
        int from = 2;
        String text = "text";

        when(itemService.searchAvailableItemsByText(anyLong(), eq(text), any(AppPageRequest.class))).thenReturn(
            List.of());

        mockMvc
            .perform(
                get(CONTROLLER_URL + "/search")
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .param("from", String.valueOf(from))
                    .param(text, text)
            )
            .andExpect(status().isOk());

        verify(itemService).searchAvailableItemsByText(userId, text, new AppPageRequest(from, DEFAULT_SIZE));
    }

    @Test
    @SneakyThrows
    void searchAvailableItems_whenFromHasNegativeValue_thenReturnedBadRequest() {
        int from = -1;
        String text = "text";

        mockMvc.perform(
                get(CONTROLLER_URL + "/search")
                    .header(CustomHeaders.USER_ID_HEADER, 1L)
                    .param("from", String.valueOf(from))
                    .param("text", text)
            )
            .andExpect(status().isBadRequest());

        verify(itemService, never()).searchAvailableItemsByText(anyLong(), anyString(), any(AppPageRequest.class));

    }

    @Test
    @SneakyThrows
    void searchAvailableItems_whenSizeHasNegativeValue_thenReturnedBadRequest() {
        int size = -1;
        String text = "text";

        mockMvc.perform(
                get(CONTROLLER_URL + "/search")
                    .header(CustomHeaders.USER_ID_HEADER, 1L)
                    .param("size", String.valueOf(size))
                    .param("text", text)
            )
            .andExpect(status().isBadRequest());

        verify(itemService, never()).searchAvailableItemsByText(anyLong(), eq(text), any(AppPageRequest.class));
    }

    /**
     * addComment
     */
    @Test
    @SneakyThrows
    void addComment_whenUserIdHeaderIsPresentedAndItemIdIsPresentedAndCommentIsValid_thenStatusIsOk() {
        String text = "text";
        CommentRequestDto commentRequestDto = CommentRequestDto.builder().text(text).build();
        CommentResponseDto commentResponseDto = CommentResponseDto.builder().text(text).build();
        long itemId = 1L;
        long userId = 1L;

        when(itemService.addComment(userId, itemId, commentRequestDto)).thenReturn(
            commentResponseDto);

        String json = mockMvc.perform(
                post(CONTROLLER_URL + "/{itemId}/comment", itemId)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(commentRequestDto))
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentResponseDto), json);
    }

    @Test
    @SneakyThrows
    void addComment_whenCommentIsNotValid_thenReturnedBadRequest() {
        CommentRequestDto commentRequestDto = CommentRequestDto.builder().build();
        long itemId = 1L;
        long userId = 1L;

        mockMvc.perform(
                post(CONTROLLER_URL + "/{itemId}/comment", itemId)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(commentRequestDto))
            )
            .andDo(print())
            .andExpect(status().isBadRequest());
    }
}