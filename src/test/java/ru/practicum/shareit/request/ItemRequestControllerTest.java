package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Profile("test")
class ItemRequestControllerTest {

    @InjectMocks
    private ItemRequestController itemRequestController;

    @Mock
    private ItemRequestService itemRequestService;


    @Test
    void getAllOwnRequests_whenInvoked_thenResponseStatusOkAndListOfRequestsInBody() {
        long userId = 1L;
        ItemRequestResponseDto itemRequestResponseDto = ItemRequestResponseDto.builder().build();
        List<ItemRequestResponseDto> requests = List.of(itemRequestResponseDto);

        when(itemRequestService.getAllOwnRequests(userId))
            .thenReturn(requests);

        List<ItemRequestResponseDto> response = itemRequestController.getAllOwnRequests(userId);

        assertEquals(requests, response);
    }

    @Test
    void createNewRequest_whenInvoked_thenResponseStatusCreatedAndItemRequestInBody() {
        long userId = 1L;
        ItemRequestRequestDto itemRequestRequestDto = ItemRequestRequestDto.builder().build();
        ItemRequestResponseDto itemRequestResponseDto = ItemRequestResponseDto.builder().build();

        when(itemRequestService.createNewRequest(itemRequestRequestDto, userId))
            .thenReturn(itemRequestResponseDto);

        ItemRequestResponseDto response =
            itemRequestController.createNewRequest(userId, itemRequestRequestDto);

        assertEquals(itemRequestResponseDto, response);
    }

    @Test
    void getAllRequests_whenInvoked_thenResponseStatusOKAndListOfRequestsInBody() {
        long userId = 1L;
        ItemRequestResponseDto itemRequestResponseDto = ItemRequestResponseDto.builder().build();
        List<ItemRequestResponseDto> requests = List.of(itemRequestResponseDto);

        when(itemRequestService.getAllRequests(any(PageRequest.class), eq(userId)))
            .thenReturn(requests);

        List<ItemRequestResponseDto> response = itemRequestController.getAllRequests(userId, 0, 10);

        assertEquals(requests, response);
    }

    @Test
    void getRequestById_whenInvoked_thenResponseStatusOKAndItemRequestInBody() {
        long userId = 1L;
        long requestId = 1L;
        ItemRequestResponseDto itemRequestResponseDto = ItemRequestResponseDto.builder().build();

        when(itemRequestService.getRequestById(requestId, userId))
            .thenReturn(itemRequestResponseDto);

        ItemRequestResponseDto response =
            itemRequestController.getRequestById(requestId, userId);

        assertEquals(itemRequestResponseDto, response);
    }
}