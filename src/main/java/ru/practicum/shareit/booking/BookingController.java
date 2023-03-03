package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.constants.BookingState;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constant.CustomHeaders;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @Valid @RequestBody BookingRequestDto bookingRequestDto
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
    public List<BookingResponseDto> getCurrentUserBookings(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestParam(required = false, defaultValue = "ALL") BookingState state
    ) {
        return bookingService.getCurrentUserBookings(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getCurrentUserAllItemsBookings(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestParam(required = false, defaultValue = "ALL") BookingState state
    ) {
        return bookingService.getCurrentUserAllItemsBookings(state, userId);
    }
}
