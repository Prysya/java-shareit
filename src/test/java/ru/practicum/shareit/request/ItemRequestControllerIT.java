package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.constant.CustomHeaders;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@ExtendWith(MockitoExtension.class)
class ItemRequestControllerIT {
    private static final String CONTROLLER_URL = "/requests";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService itemRequestService;

    /**
     * getAllOwnRequests
     */
    @Test
    @SneakyThrows
    void getAllOwnRequests_whenUserIdIsPresented_thenReturnedStatusOkAndListOfItemRequests() {
        long userId = 1L;

        mockMvc
            .perform(
                get(CONTROLLER_URL)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
            )
            .andExpect(status().isOk());

        verify(itemRequestService).getAllOwnRequests(userId);
    }

    @Test
    @SneakyThrows
    void getAllOwnRequests_whenUserIdIsNotPresented_thenReturnedBadRequest() {

        mockMvc
            .perform(
                get(CONTROLLER_URL)
            )
            .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).getAllOwnRequests(anyLong());
    }

    /**
     * createNewRequest
     */
    @Test
    @SneakyThrows
    void createNewRequest_whenUserIdIsPresentedAndRequestIsValid_thenReturnedStatusIsCreatedAndItemRequest() {
        long userId = 1L;
        String desc = "test";
        ItemRequestResponseDto itemRequestResponseDto = ItemRequestResponseDto.builder().description(desc).build();
        ItemRequestRequestDto itemRequestRequestDto = ItemRequestRequestDto.builder().description(desc).build();

        when(itemRequestService.createNewRequest(any(ItemRequestRequestDto.class), anyLong())).thenReturn(
            itemRequestResponseDto);

        String json = mockMvc
            .perform(
                post(CONTROLLER_URL)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .content(objectMapper.writeValueAsString(itemRequestRequestDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.description", is(itemRequestResponseDto.getDescription())))
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestResponseDto), json);
    }

    @Test
    @SneakyThrows
    void createNewRequest_whenUserIdIsPresentedAndRequestIsNotValid_thenReturnedBadRequest() {
        long userId = 1L;
        String desc = "";
        ItemRequestRequestDto itemRequestRequestDto = ItemRequestRequestDto.builder().description(desc).build();


        mockMvc
            .perform(
                post(CONTROLLER_URL)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .content(objectMapper.writeValueAsString(itemRequestRequestDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).createNewRequest(any(ItemRequestRequestDto.class), anyLong());

    }

    /**
     * getAllRequests
     */
    @Test
    @SneakyThrows
    void getAllRequests_whenUserIdIsPresented_thenReturnedStatusOk() {
        long userId = 1L;

        mockMvc
            .perform(
                get(CONTROLLER_URL + "/all")
                    .header(CustomHeaders.USER_ID_HEADER, userId)
            )
            .andExpect(status().isOk());

        verify(itemRequestService).getAllRequests(any(PageRequest.class), eq(userId));
    }

    @Test
    @SneakyThrows
    void getAllRequests_whenUserIdIsNotPresented_thenReturnedBadRequest() {
        long userId = 1L;

        mockMvc
            .perform(
                get(CONTROLLER_URL + "/all")
            )
            .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).getAllRequests(any(PageRequest.class), eq(userId));
    }

    /**
     * getRequestById
     */
    @Test
    @SneakyThrows
    void getRequestById_whenUserIdIsPresented_thenReturnedStatusOk() {
        long userId = 1L;
        long requestId = 1L;


        mockMvc
            .perform(
                get(CONTROLLER_URL + "/{requestId}", requestId)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
            )
            .andExpect(status().isOk());

        verify(itemRequestService).getRequestById(requestId, userId);
    }
}