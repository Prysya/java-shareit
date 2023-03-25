package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.constants.BookingState;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.constant.AppErrorMessage;
import ru.practicum.shareit.constant.CustomHeaders;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createBooking(
        @RequestHeader(name = CustomHeaders.USER_ID_HEADER) long userId,
        @Valid @RequestBody BookingRequestDto bookingRequestDto
    ) {
        log.info("Create booking={}, userId={}", bookingRequestDto, userId);
        return bookingClient.createNewBooking(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestParam(required = false) boolean approved,
        @PathVariable long bookingId
    ) {
        log.info("Patch bookingId={}, approved={}, userId={}", bookingId, approved, userId);
        return bookingClient.updateBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @PathVariable Long bookingId
    ) {
        log.info("Get booking by id {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getCurrentUserBookings(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestParam(required = false, defaultValue = "ALL") BookingState state,
        @RequestParam(defaultValue = "0")
        @PositiveOrZero(message = AppErrorMessage.PAGE_IS_NOT_POSITIVE)
        Integer from,
        @RequestParam(defaultValue = "10")
        @Positive(message = AppErrorMessage.SIZE_IS_NOT_POSITIVE)
        Integer size
    ) {
        log.info("Get bookings with state={}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size, false);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getCurrentUserAllItemsBookings(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestParam(required = false, defaultValue = "ALL") BookingState state,
        @RequestParam(defaultValue = "0")
        @PositiveOrZero(message = AppErrorMessage.PAGE_IS_NOT_POSITIVE)
        Integer from,
        @RequestParam(defaultValue = "10")
        @Positive(message = AppErrorMessage.SIZE_IS_NOT_POSITIVE)
        Integer size
    ) {
        log.info("Get owner bookings with state={}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size, true);
    }


}
