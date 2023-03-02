package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingItemResponseDTO;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.user.dto.UserDTO;

@Component
public class BookingMapper {
    public BookingResponseDto toResponseDto(Booking booking, UserDTO bookerDTO, ItemDTO itemDTO) {
        return BookingResponseDto.builder()
            .id(booking.getId())
            .status(booking.getStatus())
            .start(booking.getStart())
            .end(booking.getEnd())
            .booker(bookerDTO)
            .item(itemDTO)
            .build();
    }

    public BookingItemResponseDTO toItemResponseDto(Booking booking, UserDTO bookerDTO) {
        return BookingItemResponseDTO.builder()
            .id(booking.getId())
            .status(booking.getStatus())
            .start(booking.getStart())
            .end(booking.getEnd())
            .bookerId(bookerDTO.getId())
            .build();
    }

    public Booking toBooking(BookingRequestDto bookingRequestDto) {
        return Booking.builder()
            .start(bookingRequestDto.getStart())
            .end(bookingRequestDto.getEnd())
            .build();
    }
}
