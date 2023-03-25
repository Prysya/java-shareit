package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.AppErrorMessage;
import ru.practicum.shareit.constant.CustomHeaders;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getAllOwnRequests(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId
    ) {
        log.info("Get all own requests with userId={}", userId);
        return itemRequestClient.getAllOwnRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createNewRequest(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @Valid @RequestBody ItemRequestRequestDto itemRequestRequestDto
    ) {
        log.info("Create request with userId={}, request={}", userId, itemRequestRequestDto);
        return itemRequestClient.createNewRequest(userId, itemRequestRequestDto);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestParam(defaultValue = "0")
        @PositiveOrZero(message = AppErrorMessage.PAGE_IS_NOT_POSITIVE)
        Integer from,
        @RequestParam(defaultValue = "10")
        @Positive(message = AppErrorMessage.SIZE_IS_NOT_POSITIVE)
        Integer size
    ) {
        log.info("Get all requests with userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
        @PathVariable long requestId,
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId
    ) {
        log.info("Get request by id with userId={}, requestId={}", userId, requestId);
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
