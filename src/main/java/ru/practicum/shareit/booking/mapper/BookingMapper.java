package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingItemResponseDTO;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserDTO;


@UtilityClass
public class BookingMapper {
    public static BookingResponseDto toResponseDto(Booking booking, UserDTO bookerDTO, ItemResponseDto itemDTO) {
        return BookingResponseDto.builder()
            .id(booking.getId())
            .status(booking.getStatus())
            .start(booking.getStart())
            .end(booking.getEnd())
            .booker(bookerDTO)
            .item(itemDTO)
            .build();
    }

    public static BookingItemResponseDTO toItemResponseDto(Booking booking, UserDTO bookerDTO) {
        return BookingItemResponseDTO.builder()
            .id(booking.getId())
            .status(booking.getStatus())
            .start(booking.getStart())
            .end(booking.getEnd())
            .bookerId(bookerDTO.getId())
            .build();
    }

    public static Booking toBooking(BookingRequestDto bookingRequestDto) {
        return Booking.builder()
            .start(bookingRequestDto.getStart())
            .end(bookingRequestDto.getEnd())
            .build();
    }
}
