package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.AppPageRequest;
import ru.practicum.shareit.constant.CustomHeaders;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestResponseDto> getAllOwnRequests(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId
    ) {
        return itemRequestService.getAllOwnRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestResponseDto createNewRequest(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestBody ItemRequestRequestDto itemRequestRequestDto
    ) {
        return itemRequestService.createNewRequest(itemRequestRequestDto, userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllRequests(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestParam Integer from,
        @RequestParam Integer size
    ) {
        return itemRequestService.getAllRequests(new AppPageRequest(from, size), userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getRequestById(
        @PathVariable long requestId, @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId
    ) {
        return itemRequestService.getRequestById(requestId, userId);
    }
}
