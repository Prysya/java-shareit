package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.constants.BookingState;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.user.dto.UserDTO;

import java.util.List;

public interface BookingService {
    BookingResponseDto createNewBooking(BookingRequestDto bookingRequestDto, UserDTO userDTO, ItemDTO itemDTO);

    BookingResponseDto updateBooking(long bookingId, boolean approved, UserDTO userDTO);

    BookingResponseDto getBookingById(Long bookingId, UserDTO userDTO);

    List<BookingResponseDto> getCurrentUserBookings(BookingState state, UserDTO userDTO);

    List<BookingResponseDto> getCurrentUserAllItemsBookings(BookingState state, UserDTO userDTO);
}
