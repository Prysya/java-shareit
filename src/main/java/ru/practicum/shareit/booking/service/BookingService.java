package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.constants.BookingState;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto createNewBooking(BookingRequestDto bookingRequestDto, long itemId);

    BookingResponseDto updateBooking(long bookingId, boolean approved, long itemId);

    BookingResponseDto getBookingById(long bookingId, long itemId);

    List<BookingResponseDto> getCurrentUserBookings(BookingState state, long itemId, PageRequest pageRequest);

    List<BookingResponseDto> getCurrentUserAllItemsBookings(BookingState state, long itemId, PageRequest pageRequest);
}
