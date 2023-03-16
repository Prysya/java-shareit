package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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

        ResponseEntity<List<ItemRequestResponseDto>> response = itemRequestController.getAllOwnRequests(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(requests, response.getBody());
    }

    @Test
    void createNewRequest_whenInvoked_thenResponseStatusCreatedAndItemRequestInBody() {
        long userId = 1L;
        ItemRequestRequestDto itemRequestRequestDto = ItemRequestRequestDto.builder().build();
        ItemRequestResponseDto itemRequestResponseDto = ItemRequestResponseDto.builder().build();

        when(itemRequestService.createNewRequest(itemRequestRequestDto, userId))
            .thenReturn(itemRequestResponseDto);

        ResponseEntity<ItemRequestResponseDto> response =
            itemRequestController.createNewRequest(userId, itemRequestRequestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(itemRequestResponseDto, response.getBody());
    }

    @Test
    void getAllRequests_whenInvoked_thenResponseStatusOKAndListOfRequestsInBody() {
        long userId = 1L;
        ItemRequestResponseDto itemRequestResponseDto = ItemRequestResponseDto.builder().build();
        List<ItemRequestResponseDto> requests = List.of(itemRequestResponseDto);

        when(itemRequestService.getAllRequests(any(PageRequest.class), eq(userId)))
            .thenReturn(requests);

        ResponseEntity<List<ItemRequestResponseDto>> response = itemRequestController.getAllRequests(userId, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(requests, response.getBody());
    }

    @Test
    void getRequestById_whenInvoked_thenResponseStatusOKAndItemRequestInBody() {
        long userId = 1L;
        long requestId = 1L;
        ItemRequestResponseDto itemRequestResponseDto = ItemRequestResponseDto.builder().build();

        when(itemRequestService.getRequestById(requestId, userId))
            .thenReturn(itemRequestResponseDto);

        ResponseEntity<ItemRequestResponseDto> response =
            itemRequestController.getRequestById(requestId, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(itemRequestResponseDto, response.getBody());
    }
}