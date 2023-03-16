package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.constants.BookingErrorMessage;
import ru.practicum.shareit.booking.constants.BookingState;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.AppPageRequest;
import ru.practicum.shareit.constant.CustomHeaders;
import ru.practicum.shareit.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookingController.class)
@ExtendWith(MockitoExtension.class)
class BookingControllerIT {
    private static final String CONTROLLER_URL = "/bookings";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingRequestDto validBookingRequest;

    @BeforeEach
    public void beforeEach() {
        validBookingRequest = BookingRequestDto.builder()
            .start(LocalDateTime.now().plusDays(2))
            .end(LocalDateTime.now().plusDays(10))
            .itemId(1L)
            .build();
    }

    /**
     * getBookingById
     */
    @Test
    @SneakyThrows
    void getBookingById_whenUserIdHeaderIsPresentedAndBookingIdHasCorrectType_thenStatusIsOK() {
        long bookingId = 1L;
        long userId = 1L;

        mockMvc
            .perform(
                get(CONTROLLER_URL + "/{bookingId}", bookingId)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
            )
            .andExpect(status().isOk());

        verify(bookingService).getBookingById(bookingId, userId);
    }

    @Test
    @SneakyThrows
    void getBookingById_whenUserIdHeaderIsNotPresented_thenReturnedBadRequest() {
        long bookingId = 1L;
        long userId = 1L;

        mockMvc
            .perform(
                get(CONTROLLER_URL + "/{bookingId}", bookingId)
            )
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
            .andExpect(result -> assertEquals(
                String.format(BookingErrorMessage.BOOKING_NOT_FOUND, bookingId),
                Objects.requireNonNull(result.getResolvedException()).getMessage())
            );
        ;

        verify(bookingService, never()).getBookingById(bookingId, userId);
    }

    @Test
    @SneakyThrows
    void getBookingById_whenBookingIdHasWrongType_thenReturnedBadRequest() {
        String bookingId = "String";
        long userId = 1L;

        mockMvc
            .perform(
                get(CONTROLLER_URL + "/{bookingId}", bookingId)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
            )
            .andExpect(status().isBadRequest());

        verify(bookingService, never()).getBookingById(anyLong(), eq(userId));
    }

    /**
     * createBooking
     */
    @Test
    @SneakyThrows
    void createBooking_whenBookingIsValidAndUserIdIsPresented_thenReturnedCreatedBookingAndStatusIsCreated() {
        long userId = 1L;
        BookingResponseDto bookingResponseDto = BookingResponseDto.builder().build();

        when(bookingService.createNewBooking(any(BookingRequestDto.class), eq(userId))).thenReturn(bookingResponseDto);

        String json = mockMvc
            .perform(
                post(CONTROLLER_URL)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validBookingRequest))
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingResponseDto), json);
    }

    @Test
    @SneakyThrows
    void createBooking_whenBookingIsNotValid_thenReturnedBadRequest() {
        long userId = 1L;

        mockMvc
            .perform(
                post(CONTROLLER_URL)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(BookingRequestDto.builder().build()))
            )
            .andExpect(status().isBadRequest());

        verify(bookingService, never()).createNewBooking(any(BookingRequestDto.class), eq(userId));
    }

    /**
     * updateBooking
     */
    @Test
    @SneakyThrows
    void updateBooking_whenUserIdHeaderIsPresentedAndBookingIdIsPresentAndApprovedIsNotPresent_thenStatusIsOKAndApprovedInvokedWithFalse() {
        long bookingId = 1L;
        long userId = 1L;
        boolean approved = false;
        BookingResponseDto bookingResponseDto = BookingResponseDto.builder().build();

        when(bookingService.updateBooking(bookingId, approved, userId)).thenReturn(bookingResponseDto);

        mockMvc
            .perform(
                patch(CONTROLLER_URL + "/{bookingId}", bookingId)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
            )
            .andExpect(status().isOk());

        verify(bookingService).updateBooking(bookingId, approved, userId);
    }

    @Test
    @SneakyThrows
    void updateBooking_whenUserIdHeaderIsPresentedAndBookingIdIsPresentAndApprovedIsTrue_thenStatusIsOKAndApprovedInvokedWithTrue() {
        long bookingId = 1L;
        long userId = 1L;
        boolean approved = true;
        BookingResponseDto bookingResponseDto = BookingResponseDto.builder().build();

        when(bookingService.updateBooking(bookingId, approved, userId)).thenReturn(bookingResponseDto);

        mockMvc
            .perform(
                patch(CONTROLLER_URL + "/{bookingId}", bookingId)
                    .param("approved", String.valueOf(approved))
                    .header(CustomHeaders.USER_ID_HEADER, userId)
            )
            .andExpect(status().isOk());

        verify(bookingService).updateBooking(bookingId, approved, userId);
    }

    @Test
    @SneakyThrows
    void updateBooking_whenUserIdIsNotPresented_thenReturnedBadRequest() {
        long bookingId = 1L;

        mockMvc
            .perform(
                patch(CONTROLLER_URL + "/{bookingId}", bookingId)
            )
            .andExpect(status().isBadRequest());

        verify(bookingService, never()).updateBooking(anyLong(), anyBoolean(), anyLong());
    }

    @Test
    @SneakyThrows
    void updateBooking_whenUserHasWrongType_thenReturnedBadRequest() {
        long bookingId = 1L;


        mockMvc
            .perform(
                patch(CONTROLLER_URL + "/{bookingId}", bookingId)
                    .header(CustomHeaders.USER_ID_HEADER, "userId")
            )
            .andExpect(status().isBadRequest());

        verify(bookingService, never()).updateBooking(anyLong(), anyBoolean(), anyLong());
    }

    @Test
    @SneakyThrows
    void updateBooking_whenBookingIdHasWrongType_thenReturnedBadRequest() {
        long userId = 1L;

        mockMvc
            .perform(
                patch(CONTROLLER_URL + "/{bookingId}", "BookingId")
                    .header(CustomHeaders.USER_ID_HEADER, userId)
            )
            .andExpect(status().isBadRequest());

        verify(bookingService, never()).updateBooking(anyLong(), anyBoolean(), anyLong());
    }

    /**
     * getCurrentUserBookings
     */
    @Test
    @SneakyThrows
    void getCurrentUserBookings_whenUserIdIsPresentedAndStateIsNotPresentedAndPaginationIsNotPresented_thenStatusIsOK() {
        long userId = 1L;
        BookingState defaultState = BookingState.ALL;
        AppPageRequest defaultAppPageRequest = new AppPageRequest(0, 10);
        BookingResponseDto bookingResponseDto = BookingResponseDto.builder().build();

        when(bookingService.getCurrentUserBookings(defaultState, userId, defaultAppPageRequest)).thenReturn(
            List.of(bookingResponseDto));

        mockMvc.perform(
                get(CONTROLLER_URL)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
            )
            .andExpect(status().isOk());

        verify(bookingService).getCurrentUserBookings(defaultState, userId, defaultAppPageRequest);
    }

    @Test
    @SneakyThrows
    void getCurrentUserBookings_whenUserIdIsPresentedAndStateIsPresentedAndPaginationIsNotPresented_thenStatusIsOK() {
        long userId = 1L;
        BookingState state = BookingState.REJECTED;
        AppPageRequest defaultAppPageRequest = new AppPageRequest(0, 10);
        BookingResponseDto bookingResponseDto = BookingResponseDto.builder().build();

        when(bookingService.getCurrentUserBookings(state, userId, defaultAppPageRequest)).thenReturn(
            List.of(bookingResponseDto));

        mockMvc.perform(
                get(CONTROLLER_URL)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .param("state", String.valueOf(state))
            )
            .andExpect(status().isOk());

        verify(bookingService).getCurrentUserBookings(state, userId, defaultAppPageRequest);
    }

    @Test
    @SneakyThrows
    void getCurrentUserBookings_whenUserIdIsPresentedAndStateIsPresentedAndPaginationIsPresented_thenStatusIsOK() {
        long userId = 1L;
        BookingState state = BookingState.REJECTED;
        int from = 1;
        int size = 2;
        AppPageRequest appPageRequest = new AppPageRequest(from, size);
        BookingResponseDto bookingResponseDto = BookingResponseDto.builder().build();

        when(bookingService.getCurrentUserBookings(state, userId, appPageRequest)).thenReturn(
            List.of(bookingResponseDto));

        mockMvc.perform(
                get(CONTROLLER_URL)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .param("state", String.valueOf(state))
                    .param("from", String.valueOf(from))
                    .param("state", String.valueOf(size))
            )
            .andExpect(status().isOk());

        verify(bookingService).getCurrentUserBookings(state, userId, appPageRequest);
    }

    @Test
    @SneakyThrows
    void getCurrentUserBookings_whenUserIdIsNotPresented_thenReturnedBadRequest() {
        mockMvc.perform(
                get(CONTROLLER_URL)
            )
            .andExpect(status().isBadRequest());

        verify(bookingService, never()).getCurrentUserBookings(
            any(BookingState.class), anyLong(), any(AppPageRequest.class));
    }

    @Test
    @SneakyThrows
    void getCurrentUserBookings_whenUserIdIsHasWrongType_thenReturnedBadRequest() {
        mockMvc.perform(
                get(CONTROLLER_URL)
                    .header(CustomHeaders.USER_ID_HEADER, "userId")
            )
            .andExpect(status().isBadRequest());

        verify(bookingService, never()).getCurrentUserBookings(
            any(BookingState.class), anyLong(), any(AppPageRequest.class));
    }

    @Test
    @SneakyThrows
    void getCurrentUserBookings_whenStateHasWrongType_thenReturnedBadRequest() {
        long userId = 1L;

        mockMvc.perform(
                get(CONTROLLER_URL)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .param("state", "WRONG_STATE")
            )
            .andExpect(status().isBadRequest());

        verify(bookingService, never()).getCurrentUserBookings(
            any(BookingState.class), anyLong(), any(AppPageRequest.class));
    }

    @Test
    @SneakyThrows
    void getCurrentUserBookings_whenFromHasNegativeValue_thenReturnedBadRequest() {
        long userId = 1L;
        int from = -1;

        mockMvc.perform(
                get(CONTROLLER_URL)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .param("from", String.valueOf(from))
            )
            .andExpect(status().isBadRequest());

        verify(bookingService, never()).getCurrentUserBookings(
            any(BookingState.class), anyLong(), any(AppPageRequest.class));
    }

    @Test
    @SneakyThrows
    void getCurrentUserBookings_whenSizeHasNegativeValue_thenReturnedBadRequest() {
        long userId = 1L;
        int size = -1;

        mockMvc.perform(
                get(CONTROLLER_URL)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .param("size", String.valueOf(size))
            )
            .andExpect(status().isBadRequest());

        verify(bookingService, never()).getCurrentUserBookings(
            any(BookingState.class), anyLong(), any(AppPageRequest.class));
    }

    @Test
    @SneakyThrows
    void getCurrentUserBookings_whenSizeIsZero_thenReturnedBadRequest() {
        long userId = 1L;
        int size = 0;

        mockMvc.perform(
                get(CONTROLLER_URL)
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .param("size", String.valueOf(size))
            )
            .andExpect(status().isBadRequest());

        verify(bookingService, never()).getCurrentUserBookings(
            any(BookingState.class), anyLong(), any(AppPageRequest.class));
    }

    /**
     * getCurrentUserAllItemsBookings
     */
    @Test
    @SneakyThrows
    void getCurrentUserAllItemsBookings_whenUserIdIsPresentedAndStateIsNotPresentedAndPaginationIsNotPresented_thenStatusIsOK() {
        long userId = 1L;
        BookingState defaultState = BookingState.ALL;
        AppPageRequest defaultAppPageRequest = new AppPageRequest(0, 10);
        BookingResponseDto bookingResponseDto = BookingResponseDto.builder().build();

        when(bookingService.getCurrentUserAllItemsBookings(defaultState, userId, defaultAppPageRequest)).thenReturn(
            List.of(bookingResponseDto));

        mockMvc.perform(
                get(CONTROLLER_URL + "/owner")
                    .header(CustomHeaders.USER_ID_HEADER, userId)
            )
            .andExpect(status().isOk());

        verify(bookingService).getCurrentUserAllItemsBookings(defaultState, userId, defaultAppPageRequest);
    }

    @Test
    @SneakyThrows
    void getCurrentUserAllItemsBookings_whenUserIdIsPresentedAndStateIsPresentedAndPaginationIsNotPresented_thenStatusIsOK() {
        long userId = 1L;
        BookingState state = BookingState.REJECTED;
        AppPageRequest defaultAppPageRequest = new AppPageRequest(0, 10);
        BookingResponseDto bookingResponseDto = BookingResponseDto.builder().build();

        when(bookingService.getCurrentUserAllItemsBookings(state, userId, defaultAppPageRequest)).thenReturn(
            List.of(bookingResponseDto));

        mockMvc.perform(
                get(CONTROLLER_URL + "/owner")
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .param("state", String.valueOf(state))
            )
            .andExpect(status().isOk());

        verify(bookingService).getCurrentUserAllItemsBookings(state, userId, defaultAppPageRequest);
    }

    @Test
    @SneakyThrows
    void getCurrentUserAllItemsBookings_whenUserIdIsPresentedAndStateIsPresentedAndPaginationIsPresented_thenStatusIsOK() {
        long userId = 1L;
        BookingState state = BookingState.REJECTED;
        int from = 1;
        int size = 2;
        AppPageRequest appPageRequest = new AppPageRequest(from, size);
        BookingResponseDto bookingResponseDto = BookingResponseDto.builder().build();

        when(bookingService.getCurrentUserAllItemsBookings(state, userId, appPageRequest)).thenReturn(
            List.of(bookingResponseDto));

        mockMvc.perform(
                get(CONTROLLER_URL + "/owner")
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .param("state", String.valueOf(state))
                    .param("from", String.valueOf(from))
                    .param("state", String.valueOf(size))
            )
            .andExpect(status().isOk());

        verify(bookingService).getCurrentUserAllItemsBookings(state, userId, appPageRequest);
    }

    @Test
    @SneakyThrows
    void getCurrentUserAllItemsBookings_whenUserIdIsNotPresented_thenReturnedBadRequest() {
        mockMvc.perform(
                get(CONTROLLER_URL + "/owner")
            )
            .andExpect(status().isBadRequest());

        verify(bookingService, never()).getCurrentUserAllItemsBookings(
            any(BookingState.class), anyLong(), any(AppPageRequest.class));
    }

    @Test
    @SneakyThrows
    void getCurrentUserAllItemsBookings_whenUserIdIsHasWrongType_thenReturnedBadRequest() {
        mockMvc.perform(
                get(CONTROLLER_URL + "/owner")
                    .header(CustomHeaders.USER_ID_HEADER, "userId")
            )
            .andExpect(status().isBadRequest());

        verify(bookingService, never()).getCurrentUserAllItemsBookings(
            any(BookingState.class), anyLong(), any(AppPageRequest.class));
    }

    @Test
    @SneakyThrows
    void getCurrentUserAllItemsBookings_whenStateHasWrongType_thenReturnedBadRequest() {
        long userId = 1L;

        mockMvc.perform(
                get(CONTROLLER_URL + "/owner")
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .param("state", "WRONG_STATE")
            )
            .andExpect(status().isBadRequest());

        verify(bookingService, never()).getCurrentUserAllItemsBookings(
            any(BookingState.class), anyLong(), any(AppPageRequest.class));
    }

    @Test
    @SneakyThrows
    void getCurrentUserAllItemsBookings_whenFromHasNegativeValue_thenReturnedBadRequest() {
        long userId = 1L;
        int from = -1;

        mockMvc.perform(
                get(CONTROLLER_URL + "/owner")
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .param("from", String.valueOf(from))
            )
            .andExpect(status().isBadRequest());

        verify(bookingService, never()).getCurrentUserAllItemsBookings(
            any(BookingState.class), anyLong(), any(AppPageRequest.class));
    }

    @Test
    @SneakyThrows
    void getCurrentUserAllItemsBookings_whenSizeHasNegativeValue_thenReturnedBadRequest() {
        long userId = 1L;
        int size = -1;

        mockMvc.perform(
                get(CONTROLLER_URL + "/owner")
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .param("size", String.valueOf(size))
            )
            .andExpect(status().isBadRequest());

        verify(bookingService, never()).getCurrentUserAllItemsBookings(
            any(BookingState.class), anyLong(), any(AppPageRequest.class));
    }

    @Test
    @SneakyThrows
    void getCurrentUserAllItemsBookings_whenSizeIsZero_thenReturnedBadRequest() {
        long userId = 1L;
        int size = 0;

        mockMvc.perform(
                get(CONTROLLER_URL + "/owner")
                    .header(CustomHeaders.USER_ID_HEADER, userId)
                    .param("size", String.valueOf(size))
            )
            .andExpect(status().isBadRequest());

        verify(bookingService, never()).getCurrentUserAllItemsBookings(
            any(BookingState.class), anyLong(), any(AppPageRequest.class));
    }
}