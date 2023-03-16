package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.constants.BookingState;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.AppPageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @InjectMocks
    private BookingController bookingController;

    @Mock
    private BookingService bookingService;


    @Test
    void createBooking_whenInvoked_thenResponseStatusOkAndBookingResponseDtoInBody() {
        long userId = 1L;
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder().build();
        BookingResponseDto bookingResponseDto = BookingResponseDto.builder().build();

        Mockito
            .when(bookingService.createNewBooking(bookingRequestDto, userId))
            .thenReturn(bookingResponseDto);

        ResponseEntity<BookingResponseDto> response = bookingController.createBooking(userId, bookingRequestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(bookingResponseDto, response.getBody());
    }

    @Test
    void updateBooking_whenInvoked_thenResponseStatusOkAndBookingResponseDtoInBody() {
        long bookingId = 1L;
        boolean approved = false;
        long userId = 1L;
        BookingResponseDto bookingResponseDto = BookingResponseDto.builder().build();

        Mockito
            .when(bookingService.updateBooking(bookingId, approved, userId))
            .thenReturn(bookingResponseDto);
        ResponseEntity<BookingResponseDto> response = bookingController.updateBooking(bookingId, approved, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookingResponseDto, response.getBody());
    }

    @Test
    void getBookingById_whenInvoked_thenResponseStatusOkAndBookingResponseDtoInBody() {
        long userId = 1L;
        long bookingId = 1L;
        BookingResponseDto bookingResponseDto = BookingResponseDto.builder().build();

        Mockito
            .when(bookingService.getBookingById(userId, bookingId))
            .thenReturn(bookingResponseDto);

        ResponseEntity<BookingResponseDto> response = bookingController.getBookingById(userId, bookingId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookingResponseDto, response.getBody());
    }

    @Test
    void getCurrentUserBookings_whenInvoked_thenResponseStatusOkAndListOfBookingResponseDtoInBody() {
        long userId = 1L;
        BookingState bookingState = BookingState.ALL;
        int from = 1;
        int size = 1;
        AppPageRequest appPageRequest = new AppPageRequest(from, size);
        List<BookingResponseDto> boookingList = List.of(BookingResponseDto.builder().build());

        Mockito
            .when(bookingService.getCurrentUserBookings(bookingState, userId, appPageRequest))
            .thenReturn(boookingList);

        ResponseEntity<List<BookingResponseDto>> response =
            bookingController.getCurrentUserBookings(userId, bookingState, 1, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(boookingList, response.getBody());
    }

    @Test
    void getCurrentUserAllItemsBookings_whenInvoked_thenResponseStatusOkAndListOfBookingResponseDtoInBody() {
        long userId = 1L;
        BookingState bookingState = BookingState.ALL;
        int from = 1;
        int size = 1;
        AppPageRequest appPageRequest = new AppPageRequest(from, size);
        List<BookingResponseDto> boookingList = List.of(BookingResponseDto.builder().build());

        Mockito
            .when(bookingService.getCurrentUserAllItemsBookings(bookingState, userId, appPageRequest))
            .thenReturn(boookingList);

        ResponseEntity<List<BookingResponseDto>> response =
            bookingController.getCurrentUserAllItemsBookings(userId, bookingState, 1, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(boookingList, response.getBody());
    }
}