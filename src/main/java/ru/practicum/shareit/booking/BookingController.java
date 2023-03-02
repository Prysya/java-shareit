package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.constants.BookingState;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constant.CustomHeaders;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemService itemService;

    @PostMapping
    public BookingResponseDto createBooking(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @Valid @RequestBody BookingRequestDto bookingRequestDto
    ) {
        UserDTO userDTO = userService.getUserById(userId);
        ItemDTO itemDTO = itemService.getItemById(bookingRequestDto.getItemId(), userId);


        return bookingService.createNewBooking(bookingRequestDto, userDTO, itemDTO);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBooking(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestParam(required = false) boolean approved,
        @PathVariable long bookingId
    ) {
        UserDTO userDTO = userService.getUserById(userId);

        return bookingService.updateBooking(bookingId, approved, userDTO);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @PathVariable Long bookingId
    ) {
        UserDTO userDTO = userService.getUserById(userId);

        return bookingService.getBookingById(bookingId, userDTO);
    }

    @GetMapping
    public List<BookingResponseDto> getCurrentUserBookings(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestParam(required = false, defaultValue = "ALL") BookingState state
    ) {
        UserDTO userDTO = userService.getUserById(userId);

        return bookingService.getCurrentUserBookings(state, userDTO);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getCurrentUserAllItemsBookings(
        @RequestHeader(CustomHeaders.USER_ID_HEADER) long userId,
        @RequestParam(required = false, defaultValue = "ALL") BookingState state
    ) {
        UserDTO userDTO = userService.getUserById(userId);

        return bookingService.getCurrentUserAllItemsBookings(state, userDTO);
    }
}
