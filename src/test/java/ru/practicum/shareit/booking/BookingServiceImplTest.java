package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.constants.BookingErrorMessage;
import ru.practicum.shareit.booking.constants.BookingState;
import ru.practicum.shareit.booking.constants.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.constants.UserErrorMessage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    /* createNewBooking */
    @Test
    void createNewBooking_whenAllDataIsCorrect_thenReturnBooking() {
        boolean available = true;
        long itemId = 1L;
        long userId = 1L;
        long itemOwnerId = 2L;
        User user = User.builder().build();
        Item item = Item.builder().available(available).owner(User.builder().id(itemOwnerId).build()).build();
        Booking booking = Booking.builder()
            .booker(user)
            .item(item)
            .build();
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
            .itemId(itemId)
            .start(LocalDateTime.MIN)
            .end(LocalDateTime.now())
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(
            Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        bookingService.createNewBooking(bookingRequestDto, userId);

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createNewBooking_whenUserNotFound_thenNotFoundExceptionThrown() {
        long itemId = 1L;
        long userId = 1L;
        Booking booking = Booking.builder().build();
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
            .itemId(itemId)
            .start(LocalDateTime.now())
            .end(LocalDateTime.MIN)
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());


        assertThrows(NotFoundException.class, () ->
            bookingService.createNewBooking(bookingRequestDto, userId)
        );

        verify(itemRepository, never()).findById(userId);
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void createNewBooking_whenItemNotFound_thenNotFoundExceptionThrown() {
        long itemId = 1L;
        long userId = 1L;
        Booking booking = Booking.builder().build();
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
            .itemId(itemId)
            .start(LocalDateTime.now())
            .end(LocalDateTime.MIN)
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
            bookingService.createNewBooking(bookingRequestDto, userId)
        );

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void createNewBooking_whenItemIsNotAvailable_thenBadRequestExceptionThrown() {
        boolean available = false;
        long itemId = 1L;
        long userId = 1L;
        long itemOwnerId = 2L;
        Booking booking = Booking.builder().build();
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
            .itemId(itemId)
            .start(LocalDateTime.MIN)
            .end(LocalDateTime.now())
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(itemRepository.findById(itemId)).thenReturn(
            Optional.of(Item.builder().available(available).owner(User.builder().id(itemOwnerId).build()).build()));

        assertThrows(BadRequestException.class, () ->
            bookingService.createNewBooking(bookingRequestDto, userId)
        );

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void createNewBooking_whenStartDateIsAfterEndDate_thenBadRequestExceptionThrown() {
        boolean available = true;
        long itemId = 1L;
        long userId = 1L;
        long itemOwnerId = 2L;
        Booking booking = Booking.builder().build();
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
            .itemId(itemId)
            .start(LocalDateTime.now())
            .end(LocalDateTime.MIN)
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(itemRepository.findById(itemId)).thenReturn(
            Optional.of(Item.builder().available(available).owner(User.builder().id(itemOwnerId).build()).build()));

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
            bookingService.createNewBooking(bookingRequestDto, userId)
        );
        assertEquals(BookingErrorMessage.START_IS_LATER_THEN_END, exception.getMessage());

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void createNewBooking_whenStartDateIsEqualsEndDate_thenBadRequestExceptionThrown() {
        LocalDateTime time = LocalDateTime.now();

        boolean available = true;
        long itemId = 1L;
        long userId = 1L;
        long itemOwnerId = 2L;
        Booking booking = Booking.builder().build();
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
            .itemId(itemId)
            .start(time)
            .end(time)
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(itemRepository.findById(itemId)).thenReturn(
            Optional.of(Item.builder().available(available).owner(User.builder().id(itemOwnerId).build()).build()));

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
            bookingService.createNewBooking(bookingRequestDto, userId)
        );
        assertEquals(BookingErrorMessage.START_IS_EQUALS_END, exception.getMessage());

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void createNewBooking_whenBookerIdEqualsOwnerId_thenNotFoundExceptionThrown() {
        boolean available = true;
        long itemId = 1L;
        long userId = 1L;
        long itemOwnerId = 1L;
        Booking booking = Booking.builder().build();
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
            .itemId(itemId)
            .start(LocalDateTime.MIN)
            .end(LocalDateTime.now())
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(itemRepository.findById(itemId)).thenReturn(
            Optional.of(Item.builder().available(available).owner(User.builder().id(itemOwnerId).build()).build()));

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            bookingService.createNewBooking(bookingRequestDto, userId)
        );
        assertEquals(BookingErrorMessage.USER_OWN_ITEM, exception.getMessage());

        verify(bookingRepository, never()).save(booking);
    }


    /* updateBooking */
    @Test
    void updateBooking_whenAllDataIsCorrectAndApprovedIsFalse_thenReturnBookingWithStatusREJECTED() {
        boolean approved = false;
        BookingStatus newStatus = BookingStatus.REJECTED;
        long userId = 1L;
        long ownerId = 1L;
        long bookingId = 1L;
        long bookerId = 2L;
        User booker = User.builder().id(bookerId).build();
        Item item = Item.builder().owner(User.builder().id(ownerId).build()).build();
        Booking booking =
            Booking.builder()
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING).build();
        BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
            .status(newStatus)
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
//        when(BookingMapper.toResponseDto(booking, null, null)).thenReturn(bookingResponseDto);

        BookingResponseDto newBooking = bookingService.updateBooking(bookingId, approved, userId);

        assertEquals(newStatus, newBooking.getStatus());

        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void updateBooking_whenAllDataIsCorrectAndApprovedIsTrue_thenReturnBookingWithStatusAPPROVED() {
        boolean approved = true;
        BookingStatus newStatus = BookingStatus.APPROVED;
        long userId = 1L;
        long ownerId = 1L;
        long bookingId = 1L;
        long bookerId = 2L;
        User booker = User.builder().id(bookerId).build();
        Item item = Item.builder().owner(User.builder().id(ownerId).build()).build();
        Booking booking =
            Booking.builder()
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING).build();


        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingResponseDto newBooking = bookingService.updateBooking(bookingId, approved, userId);

        assertEquals(newStatus, newBooking.getStatus());

        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void updateBooking_whenUserNotFound_thenNotFoundExceptionThrown() {
        boolean approved = true;
        long userId = 1L;
        long bookingId = 1L;
        Booking booking = Booking.builder().build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
            bookingService.updateBooking(bookingId, approved, userId)
        );

        verify(bookingRepository, never()).findById(bookingId);
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void updateBooking_whenBookingNotFound_thenNotFoundExceptionThrown() {
        boolean approved = true;
        long userId = 1L;
        long bookingId = 1L;
        Booking booking = Booking.builder().build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
            bookingService.updateBooking(bookingId, approved, userId)
        );

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void updateBooking_whenBookingStatusIsNotWAITING_thenBadRequestExceptionThrown() {
        boolean approved = true;
        long userId = 1L;
        long ownerId = 1L;
        long bookingId = 1L;
        long bookerId = 2L;
        User booker = User.builder().id(bookerId).build();
        Item item = Item.builder().owner(User.builder().id(ownerId).build()).build();
        Booking booking =
            Booking.builder()
                .booker(booker)
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
            bookingService.updateBooking(bookingId, approved, userId)
        );
        assertEquals(
            String.format(BookingErrorMessage.STATUS_ALREADY_CHANGED, bookingId), exception.getMessage()
        );

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void updateBooking_whenBookerIdEqualsItemOwnerId_thenNotFoundExceptionThrown() {
        boolean approved = true;
        long userId = 1L;
        long ownerId = 1L;
        long bookingId = 1L;
        long bookerId = 1L;
        User booker = User.builder().id(bookerId).build();
        Item item = Item.builder().owner(User.builder().id(ownerId).build()).build();
        Booking booking =
            Booking.builder()
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            bookingService.updateBooking(bookingId, approved, userId)
        );
        assertEquals(
            BookingErrorMessage.NOT_AUTHORIZED, exception.getMessage()
        );

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void updateBooking_whenUserIdNotEqualsOwnerId_thenBadRequestExceptionThrown() {
        boolean approved = true;
        long userId = 1L;
        long ownerId = 2L;
        long bookingId = 1L;
        long bookerId = 2L;
        User booker = User.builder().id(bookerId).build();
        Item item = Item.builder().owner(User.builder().id(ownerId).build()).build();
        Booking booking =
            Booking.builder()
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
            bookingService.updateBooking(bookingId, approved, userId)
        );
        assertEquals(
            BookingErrorMessage.NOT_AUTHORIZED, exception.getMessage()
        );

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void getBookingById_whenBookingIsFoundAndUserIdEqualsBookerId_thenReturnBooking() {
        long userId = 1L;
        long bookerId = 1L;

        long ownerId = 2L;
        long bookingId = 1L;
        User booker = User.builder().id(bookerId).build();
        Item item = Item.builder().owner(User.builder().id(ownerId).build()).build();
        Booking booking =
            Booking.builder()
                .booker(booker)
                .item(item)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingResponseDto foundBooking = bookingService.getBookingById(bookingId, userId);

        assertTrue(Objects.nonNull(foundBooking));
    }

    @Test
    void getBookingById_whenBookingIsFoundAndUserIdEqualsOwnerId_thenReturnBooking() {
        long userId = 2L;
        long ownerId = 2L;

        long bookingId = 1L;
        long bookerId = 3L;
        User booker = User.builder().id(bookerId).build();
        Item item = Item.builder().owner(User.builder().id(ownerId).build()).build();
        Booking booking =
            Booking.builder()
                .booker(booker)
                .item(item)
                .build();
        BookingResponseDto bookingResponseDto = BookingResponseDto.builder().build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingResponseDto foundBooking = bookingService.getBookingById(bookingId, userId);

        assertEquals(bookingResponseDto.getId(), foundBooking.getId());
        assertEquals(bookingResponseDto.getStart(), foundBooking.getStart());
        assertEquals(bookingResponseDto.getEnd(), foundBooking.getEnd());
    }

    @Test
    void getBookingById_whenBookingIsNotFound_thenNotFoundExceptionThrown() {
        long userId = 2L;
        long bookingId = 1L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            bookingService.getBookingById(bookingId, userId)
        );

        assertEquals(String.format(BookingErrorMessage.BOOKING_NOT_FOUND, bookingId), exception.getMessage());
    }

    @Test
    void getBookingById_whenBookingIFoundButUserIdIsNotEqualsOwnerOdAndBookerId_thenNotFoundExceptionThrown() {
        long userId = 1L;
        long ownerId = 2L;
        long bookerId = 3L;

        long bookingId = 1L;
        User booker = User.builder().id(bookerId).build();
        Item item = Item.builder().owner(User.builder().id(ownerId).build()).build();
        Booking booking =
            Booking.builder()
                .booker(booker)
                .item(item)
                .build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            bookingService.getBookingById(bookingId, userId)
        );

        assertEquals(String.format(BookingErrorMessage.BOOKING_NOT_FOUND, bookingId), exception.getMessage());
    }


    /* getCurrentUserBookings */
    @Test
    void getCurrentUserBookings_whenUserIsFoundAndBookingsNotFound_thenReturnEmptyList() {
        long userId = 1L;
        BookingState state = BookingState.ALL;

        PageRequest pageRequest = PageRequest.of(1, 1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(bookingRepository.findAllBookingsByBookerId(userId, pageRequest)).thenReturn(List.of());

        List<BookingResponseDto> bookingsListFromService =
            bookingService.getCurrentUserBookings(state, userId, pageRequest);

        assertTrue(bookingsListFromService.isEmpty());
    }

    @Test
    void getCurrentUserBookings_whenUserIsFoundAndStateIsALL_thenReturnListOfBookings() {
        long userId = 1L;
        BookingState state = BookingState.ALL;

        PageRequest pageRequest = PageRequest.of(1, 1);
        Booking booking =
            Booking.builder().booker(User.builder().build()).item(Item.builder().owner(User.builder().build()).build())
                .build();
        List<Booking> bookingsList = List.of(booking);


        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(bookingRepository.findAllBookingsByBookerId(userId, pageRequest)).thenReturn(bookingsList);

        List<BookingResponseDto> bookingsListFromService =
            bookingService.getCurrentUserBookings(state, userId, pageRequest);

        assertEquals(1, bookingsListFromService.size());
    }

    @Test
    void getCurrentUserBookings_whenUserIsFoundAndStateIsCURRENT_thenReturnListOfBookings() {
        long userId = 1L;
        BookingState state = BookingState.CURRENT;

        PageRequest pageRequest = PageRequest.of(1, 1);
        Booking booking =
            Booking.builder().booker(User.builder().build()).item(Item.builder().owner(User.builder().build()).build())
                .build();
        List<Booking> bookingsList = List.of(booking);

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(bookingRepository.findCurrentBookingsByBookerId(eq(userId), any(LocalDateTime.class),
            eq(pageRequest)
        )).thenReturn(
            bookingsList);

        List<BookingResponseDto> bookingsListFromService =
            bookingService.getCurrentUserBookings(state, userId, pageRequest);

        assertEquals(1, bookingsListFromService.size());
    }

    @Test
    void getCurrentUserBookings_whenUserIsFoundAndStateIsPAST_thenReturnListOfBookings() {
        long userId = 1L;
        BookingState state = BookingState.PAST;

        PageRequest pageRequest = PageRequest.of(1, 1);
        Booking booking =
            Booking.builder().booker(User.builder().build()).item(Item.builder().owner(User.builder().build()).build())
                .build();
        List<Booking> bookingsList = List.of(booking);

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(bookingRepository.findPastBookingsByBookerId(eq(userId), any(LocalDateTime.class),
            eq(pageRequest)
        )).thenReturn(bookingsList);

        List<BookingResponseDto> bookingsListFromService =
            bookingService.getCurrentUserBookings(state, userId, pageRequest);

        assertEquals(1, bookingsListFromService.size());
    }

    @Test
    void getCurrentUserBookings_whenUserIsFoundAndStateIsFUTURE_thenReturnListOfBookings() {
        long userId = 1L;
        BookingState state = BookingState.FUTURE;

        PageRequest pageRequest = PageRequest.of(1, 1);
        Booking booking =
            Booking.builder().booker(User.builder().build()).item(Item.builder().owner(User.builder().build()).build())
                .build();
        List<Booking> bookingsList = List.of(booking);


        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(bookingRepository.findFutureBookingsByBookerId(eq(userId), any(LocalDateTime.class),
            eq(pageRequest)
        )).thenReturn(bookingsList);

        List<BookingResponseDto> bookingsListFromService =
            bookingService.getCurrentUserBookings(state, userId, pageRequest);

        assertEquals(1, bookingsListFromService.size());
    }

    @Test
    void getCurrentUserBookings_whenUserIsFoundAndStateIsWAITING_thenReturnListOfBookings() {
        long userId = 1L;
        BookingState state = BookingState.WAITING;

        PageRequest pageRequest = PageRequest.of(1, 1);
        Booking booking =
            Booking.builder().booker(User.builder().build()).item(Item.builder().owner(User.builder().build()).build())
                .build();
        List<Booking> bookingsList = List.of(booking);

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(bookingRepository.findBookingsByBookerIdAndStatus(userId, BookingStatus.WAITING, pageRequest)).thenReturn(
            bookingsList);

        List<BookingResponseDto> bookingsListFromService =
            bookingService.getCurrentUserBookings(state, userId, pageRequest);

        assertEquals(1, bookingsListFromService.size());
    }

    @Test
    void getCurrentUserBookings_whenUserIsFoundAndStateIsREJECTED_thenReturnListOfBookings() {
        long userId = 1L;
        BookingState state = BookingState.REJECTED;

        PageRequest pageRequest = PageRequest.of(1, 1);
        Booking booking =
            Booking.builder().booker(User.builder().build()).item(Item.builder().owner(User.builder().build()).build())
                .build();
        List<Booking> bookingsList = List.of(booking);


        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(bookingRepository.findBookingsByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageRequest)).thenReturn(
            bookingsList);

        List<BookingResponseDto> bookingsListFromService =
            bookingService.getCurrentUserBookings(state, userId, pageRequest);

        assertEquals(1, bookingsListFromService.size());
    }

    @Test
    void getCurrentUserBookings_whenUserIsNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        BookingState state = BookingState.ALL;

        PageRequest pageRequest = PageRequest.of(1, 1);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            bookingService.getCurrentUserBookings(state, userId, pageRequest)
        );
        assertEquals(String.format(UserErrorMessage.NOT_FOUND, userId), exception.getMessage());

        verify(bookingRepository, never()).findCurrentBookingsByBookerId(any(), any(), any());
        verify(bookingRepository, never()).findPastBookingsByBookerId(any(), any(), any());
        verify(bookingRepository, never()).findFutureBookingsByBookerId(any(), any(), any());
        verify(bookingRepository, never()).findBookingsByBookerIdAndStatus(any(), any(), any());
        verify(bookingRepository, never()).findBookingsByBookerIdAndStatus(any(), any(), any());
        verify(bookingRepository, never()).findAllBookingsByBookerId(any(), any());
    }

    /* getCurrentUserAllItemsBookings */
    @Test
    void getCurrentUserAllItemsBookings_whenUserIsFoundAndStateIsALL_thenReturnListOfBookings_whenUserIsFoundAndBookingsNotFound_thenReturnEmptyList() {
        long userId = 1L;
        BookingState state = BookingState.ALL;

        PageRequest pageRequest = PageRequest.of(1, 1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));

        List<BookingResponseDto> bookingsListFromService =
            bookingService.getCurrentUserBookings(state, userId, pageRequest);

        assertTrue(bookingsListFromService.isEmpty());
    }

    @Test
    void getCurrentUserAllItemsBookings_whenUserIsFoundAndStateIsALL_thenReturnListOfBookings() {
        long userId = 1L;
        BookingState state = BookingState.ALL;

        PageRequest pageRequest = PageRequest.of(1, 1);
        Booking booking =
            Booking.builder().booker(User.builder().build()).item(Item.builder().owner(User.builder().build()).build())
                .build();
        List<Booking> bookingsList = List.of(booking);


        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(bookingRepository.findAllBookingsByOwnerId(userId, pageRequest)).thenReturn(bookingsList);

        List<BookingResponseDto> bookingsListFromService =
            bookingService.getCurrentUserAllItemsBookings(state, userId, pageRequest);

        assertEquals(1, bookingsListFromService.size());
    }

    @Test
    void getCurrentUserAllItemsBookings_whenUserIsFoundAndStateIsCURRENT_thenReturnListOfBookings() {
        long userId = 1L;
        BookingState state = BookingState.CURRENT;

        PageRequest pageRequest = PageRequest.of(1, 1);
        Booking booking =
            Booking.builder().booker(User.builder().build()).item(Item.builder().owner(User.builder().build()).build())
                .build();
        List<Booking> bookingsList = List.of(booking);


        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(bookingRepository.findCurrentBookingsByOwnerId(eq(userId), any(LocalDateTime.class),
            eq(pageRequest)
        )).thenReturn(
            bookingsList);

        List<BookingResponseDto> bookingsListFromService =
            bookingService.getCurrentUserAllItemsBookings(state, userId, pageRequest);

        assertEquals(1, bookingsListFromService.size());
    }

    @Test
    void getCurrentUserAllItemsBookings_whenUserIsFoundAndStateIsPAST_thenReturnListOfBookings() {
        long userId = 1L;
        BookingState state = BookingState.PAST;

        PageRequest pageRequest = PageRequest.of(1, 1);
        Booking booking =
            Booking.builder().booker(User.builder().build()).item(Item.builder().owner(User.builder().build()).build())
                .build();
        List<Booking> bookingsList = List.of(booking);

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(bookingRepository.findPastBookingsByOwnerId(eq(userId), any(LocalDateTime.class),
            eq(pageRequest)
        )).thenReturn(bookingsList);

        List<BookingResponseDto> bookingsListFromService =
            bookingService.getCurrentUserAllItemsBookings(state, userId, pageRequest);

        assertEquals(1, bookingsListFromService.size());
    }

    @Test
    void getCurrentUserAllItemsBookings_whenUserIsFoundAndStateIsFUTURE_thenReturnListOfBookings() {
        long userId = 1L;
        BookingState state = BookingState.FUTURE;

        PageRequest pageRequest = PageRequest.of(1, 1);
        Booking booking =
            Booking.builder().booker(User.builder().build()).item(Item.builder().owner(User.builder().build()).build())
                .build();
        List<Booking> bookingsList = List.of(booking);

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(bookingRepository.findFutureBookingsByOwnerId(eq(userId), any(LocalDateTime.class),
            eq(pageRequest)
        )).thenReturn(bookingsList);

        List<BookingResponseDto> bookingsListFromService =
            bookingService.getCurrentUserAllItemsBookings(state, userId, pageRequest);

        assertEquals(1, bookingsListFromService.size());
    }

    @Test
    void getCurrentUserAllItemsBookings_whenUserIsFoundAndStateIsWAITING_thenReturnListOfBookings() {
        long userId = 1L;
        BookingState state = BookingState.WAITING;

        PageRequest pageRequest = PageRequest.of(1, 1);
        Booking booking =
            Booking.builder().booker(User.builder().build()).item(Item.builder().owner(User.builder().build()).build())
                .build();
        List<Booking> bookingsList = List.of(booking);

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(bookingRepository.findBookingsByOwnerIdAndStatus(userId, BookingStatus.WAITING, pageRequest)).thenReturn(
            bookingsList);

        List<BookingResponseDto> bookingsListFromService =
            bookingService.getCurrentUserAllItemsBookings(state, userId, pageRequest);

        assertEquals(1, bookingsListFromService.size());
    }

    @Test
    void getCurrentUserAllItemsBookings_whenUserIsFoundAndStateIsREJECTED_thenReturnListOfBookings() {
        long userId = 1L;
        BookingState state = BookingState.REJECTED;

        PageRequest pageRequest = PageRequest.of(1, 1);
        Booking booking =
            Booking.builder().booker(User.builder().build()).item(Item.builder().owner(User.builder().build()).build())
                .build();
        List<Booking> bookingsList = List.of(booking);

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(bookingRepository.findBookingsByOwnerIdAndStatus(userId, BookingStatus.REJECTED, pageRequest)).thenReturn(
            bookingsList);

        List<BookingResponseDto> bookingsListFromService =
            bookingService.getCurrentUserAllItemsBookings(state, userId, pageRequest);

        assertEquals(1, bookingsListFromService.size());
    }

    @Test
    void getCurrentUserAllItemsBookings_whenUserIsNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        BookingState state = BookingState.ALL;

        PageRequest pageRequest = PageRequest.of(1, 1);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            bookingService.getCurrentUserAllItemsBookings(state, userId, pageRequest)
        );
        assertEquals(String.format(UserErrorMessage.NOT_FOUND, userId), exception.getMessage());

        verify(bookingRepository, never()).findCurrentBookingsByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findPastBookingsByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findFutureBookingsByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findBookingsByOwnerIdAndStatus(any(), any(), any());
        verify(bookingRepository, never()).findBookingsByOwnerIdAndStatus(any(), any(), any());
        verify(bookingRepository, never()).findAllBookingsByOwnerId(any(), any());
    }
}