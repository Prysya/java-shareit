package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.constants.BookingState;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.AppPageRequest;
import ru.practicum.shareit.constant.CustomHeaders;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto createBooking(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestBody BookingRequestDto bookingRequestDto
    ) {
        return bookingService.createNewBooking(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBooking(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestParam(required = false) boolean approved,
        @PathVariable long bookingId
    ) {
        return bookingService.updateBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @PathVariable Long bookingId
    ) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookings(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestParam BookingState state,
        @RequestParam Integer from,
        @RequestParam Integer size
    ) {
        return bookingService.getBookings(state, userId, new AppPageRequest(from, size));
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestParam BookingState state,
        @RequestParam Integer from,
        @RequestParam Integer size
    ) {
        return bookingService.getOwnerBookings(state, userId, new AppPageRequest(from, size));
    }
}
