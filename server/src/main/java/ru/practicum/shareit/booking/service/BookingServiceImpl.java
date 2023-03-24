package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.constants.BookingErrorMessage;
import ru.practicum.shareit.booking.constants.BookingState;
import ru.practicum.shareit.booking.constants.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exception.OutOfStateInStrategyException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.strategy.BookingParams;
import ru.practicum.shareit.booking.strategy.get_bookings.BookingsSearch;
import ru.practicum.shareit.booking.strategy.get_owner_bookings.OwnerBookingsSearch;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.constants.ItemErrorMessage;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.constants.UserErrorMessage;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final List<BookingsSearch> bookingsSearches;
    private final List<OwnerBookingsSearch> ownerBookingsSearches;
    private final EnumMap<BookingState, BookingsSearch> bookingSearchesMap = new EnumMap<>(BookingState.class);
    private final EnumMap<BookingState, OwnerBookingsSearch> ownerBookingSearchesMap = new EnumMap<>(BookingState.class);

    @PostConstruct
    void init() {
        bookingsSearches.forEach(
            bookingsSearch -> bookingSearchesMap.put(
                bookingsSearch.getState(),
                bookingsSearch
            ));
        ownerBookingsSearches.forEach(
            ownerBookingsSearch -> ownerBookingSearchesMap.put(
                ownerBookingsSearch.getState(),
                ownerBookingsSearch
            ));
    }


    @Override
    @Transactional
    public BookingResponseDto createNewBooking(BookingRequestDto bookingRequestDto, long userId) {
        User user = checkAndReturnUser(userId);
        Item item = checkAndReturnItem(bookingRequestDto.getItemId());

        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new BadRequestException(String.format(BookingErrorMessage.ITEM_IS_NOT_AVAILABLE, item.getId()));
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException(BookingErrorMessage.USER_OWN_ITEM);
        }

        Booking booking = BookingMapper.toBooking(bookingRequestDto);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(user);
        booking.setItem(item);

        Booking newBooking = bookingRepository.save(booking);

        return mapBookingToDTO(newBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto updateBooking(
        long bookingId, boolean approved, long userId
    ) {
        checkAndReturnUser(userId);
        Booking booking = checkAndReturnBooking(bookingId);

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BadRequestException(String.format(BookingErrorMessage.STATUS_ALREADY_CHANGED, bookingId));
        }

        if (booking.getBooker().getId().equals(userId)) {
            throw new NotFoundException(BookingErrorMessage.NOT_AUTHORIZED);
        }

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new BadRequestException(BookingErrorMessage.NOT_AUTHORIZED);
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return mapBookingToDTO(booking);
    }

    @Override
    public BookingResponseDto getBookingById(long bookingId, long userId) {
        Booking booking = checkAndReturnBooking(bookingId);

        if (booking.getBooker().getId().equals(userId)
            || booking.getItem().getOwner().getId().equals(userId)
        ) {
            return mapBookingToDTO(booking);
        }

        throw new NotFoundException(String.format(BookingErrorMessage.BOOKING_NOT_FOUND, bookingId));
    }

    @Override
    public List<BookingResponseDto> getBookings(BookingState state, long userId, PageRequest pageRequest) {
        checkAndReturnUser(userId);

        BookingsSearch bookingsSearch = bookingSearchesMap.get(state);

        if (Objects.isNull(bookingsSearch)) {
            throw new OutOfStateInStrategyException("bookingsSearch", state);
        }

        List<Booking> bookings = bookingsSearch.search(new BookingParams(userId, pageRequest));

        return mapBookingToDTO(bookings);
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(
        BookingState state, long ownerId, PageRequest pageRequest
    ) {
        checkAndReturnUser(ownerId);

        OwnerBookingsSearch ownerBookingsSearch = ownerBookingSearchesMap.get(state);

        if (Objects.isNull(ownerBookingsSearch)) {
            throw new OutOfStateInStrategyException("ownerBookingsSearch", state);
        }

        List<Booking> bookings = ownerBookingsSearch.search(new BookingParams(ownerId, pageRequest));

        return mapBookingToDTO(bookings);
    }

    private BookingResponseDto mapBookingToDTO(Booking booking) {
        return BookingMapper.toResponseDto(booking, UserMapper.toDto(booking.getBooker()),
            ItemMapper.toResponseDto(booking.getItem(), UserMapper.toDto(booking.getItem().getOwner()))
        );
    }

    private List<BookingResponseDto> mapBookingToDTO(List<Booking> bookings) {
        return bookings.stream().map(this::mapBookingToDTO).collect(Collectors.toList());
    }

    private User checkAndReturnUser(long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format(UserErrorMessage.NOT_FOUND, userId)));
    }

    private Item checkAndReturnItem(long itemId) {
        return itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException(String.format(ItemErrorMessage.NOT_FOUND, itemId)));
    }

    private Booking checkAndReturnBooking(long bookingId) {
        return bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException(String.format(BookingErrorMessage.BOOKING_NOT_FOUND, bookingId)));
    }
}
