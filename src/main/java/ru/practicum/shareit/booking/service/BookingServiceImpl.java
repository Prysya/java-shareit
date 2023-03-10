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
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingMapper bookingMapper;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    @Transactional
    public BookingResponseDto createNewBooking(BookingRequestDto bookingRequestDto, long userId) {
        User user = checkAndReturnUser(userId);
        Item item = checkAndReturnItem(bookingRequestDto.getItemId());

        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new BadRequestException(String.format(BookingErrorMessage.ITEM_IS_NOT_AVAILABLE, item.getId()));
        }

        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new BadRequestException(BookingErrorMessage.START_IS_LATER_THEN_END);
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException(BookingErrorMessage.USER_OWN_ITEM);
        }

        Booking booking = bookingMapper.toBooking(bookingRequestDto);
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

        return mapBookingToDTO(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getBookingById(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException(String.format(BookingErrorMessage.BOOKING_NOT_FOUND, bookingId));
        });

        if (booking.getBooker().getId().equals(userId) ||
            booking.getItem().getOwner().getId().equals(userId)) {
            return mapBookingToDTO(booking);
        }

        throw new NotFoundException(String.format(BookingErrorMessage.BOOKING_NOT_FOUND, bookingId));
    }

    @Override
    public List<BookingResponseDto> getCurrentUserBookings(BookingState state, long userId, PageRequest pageRequest) {
        checkAndReturnUser(userId);
        LocalDateTime currentTime = LocalDateTime.now();

        List<Booking> bookings;

        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByBookerId(userId, currentTime, pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findPastBookingsByBookerId(userId, currentTime, pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookingsByBookerId(userId, currentTime, pageRequest);
                break;
            case WAITING:
                bookings =
                    bookingRepository.findBookingsByBookerIdAndStatus(userId, BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings =
                    bookingRepository.findBookingsByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageRequest);
                break;
            case ALL:
            default:
                bookings = bookingRepository.findAllBookingsByBookerId(userId, pageRequest);
        }

        return mapBookingToDTO(bookings);
    }

    @Override
    public List<BookingResponseDto> getCurrentUserAllItemsBookings(
        BookingState state, long ownerId, PageRequest pageRequest
    ) {
        checkAndReturnUser(ownerId);
        LocalDateTime currentTime = LocalDateTime.now();

        List<Booking> bookings;

        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByOwnerId(ownerId, currentTime, pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findPastBookingsByOwnerId(ownerId, currentTime, pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookingsByOwnerId(ownerId, currentTime, pageRequest);
                break;
            case WAITING:
                bookings =
                    bookingRepository.findBookingsByOwnerIdAndStatus(ownerId, BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings =
                    bookingRepository.findBookingsByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, pageRequest);
                break;
            case ALL:
            default:
                bookings = bookingRepository.findAllBookingsByOwnerId(ownerId, pageRequest);
        }

        return mapBookingToDTO(bookings);
    }

    private BookingResponseDto mapBookingToDTO(Booking booking) {
        return bookingMapper.toResponseDto(booking, userMapper.toDto(booking.getBooker()),
            itemMapper.toResponseDto(booking.getItem(), userMapper.toDto(booking.getItem().getOwner()))
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
