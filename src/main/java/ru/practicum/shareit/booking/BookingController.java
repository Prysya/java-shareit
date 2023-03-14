package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.constants.BookingState;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.AppPageRequest;
import ru.practicum.shareit.constant.AppErrorMessage;
import ru.practicum.shareit.constant.CustomHeaders;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @Valid @RequestBody BookingRequestDto bookingRequestDto
    ) {
        return new ResponseEntity<>(bookingService.createNewBooking(bookingRequestDto, userId), HttpStatus.CREATED);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> updateBooking(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestParam(required = false) boolean approved,
        @PathVariable long bookingId
    ) {
        return ResponseEntity.ok(bookingService.updateBooking(bookingId, approved, userId));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBookingById(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @PathVariable Long bookingId
    ) {
        return ResponseEntity.ok(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getCurrentUserBookings(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestParam(required = false, defaultValue = "ALL") BookingState state,
        @RequestParam(defaultValue = "0")
        @PositiveOrZero(message = AppErrorMessage.PAGE_IS_NOT_POSITIVE)
        Integer from,
        @RequestParam(defaultValue = "10")
        @Positive(message = AppErrorMessage.SIZE_IS_NOT_POSITIVE)
        Integer size
    ) {
        return ResponseEntity.ok(bookingService.getCurrentUserBookings(state, userId, new AppPageRequest(from, size)));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getCurrentUserAllItemsBookings(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestParam(required = false, defaultValue = "ALL") BookingState state,
        @RequestParam(defaultValue = "0")
        @PositiveOrZero(message = AppErrorMessage.PAGE_IS_NOT_POSITIVE)
        Integer from,
        @RequestParam(defaultValue = "10")
        @Positive(message = AppErrorMessage.SIZE_IS_NOT_POSITIVE)
        Integer size
    ) {
        return ResponseEntity.ok(
            bookingService.getCurrentUserAllItemsBookings(state, userId, new AppPageRequest(from, size)));
    }
}
