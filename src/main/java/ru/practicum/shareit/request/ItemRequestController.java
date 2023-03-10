package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.AppPageRequest;
import ru.practicum.shareit.constant.AppErrorMessage;
import ru.practicum.shareit.constant.CustomHeaders;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
    public ItemRequestResponseDto createNewRequest(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @Valid @RequestBody ItemRequestRequestDto itemRequestRequestDto
    ) {
        return itemRequestService.createNewRequest(itemRequestRequestDto, userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllRequests(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestParam(defaultValue = "0")
        @PositiveOrZero(message = AppErrorMessage.PAGE_IS_NOT_POSITIVE)
        Integer from,
        @RequestParam(defaultValue = "10")
        @Positive(message = AppErrorMessage.SIZE_IS_NOT_POSITIVE)
        Integer size
    ) {
        return itemRequestService.getAllRequests(new AppPageRequest(from, size), userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getRequestById(
        @PathVariable long requestId,
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId
    ) {
        return itemRequestService.getRequestById(requestId, userId);
    }
}
