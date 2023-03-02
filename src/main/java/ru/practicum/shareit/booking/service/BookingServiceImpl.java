package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.constants.BookingState;
import ru.practicum.shareit.booking.constants.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    public static final String USER_OWN_ITEM = "Пользователь не может создать бронирование на свою же вещь";
    public static final String BOOKING_NOT_FOUND = "Бронирование с id = %d, не найдено";
    public static final String STATUS_ALREADY_CHANGED =
        "Невозможно изменить статус бронирования с id = %d. Статус уже ранее был изменен";
    public static final String NOT_AUTHORIZED = "Изменение статуса вещи может быть выполнено только владельцем";
    public static final String ITEM_IS_NOT_AVAILABLE = "Вещь c id = %d не доступна";
    public static final String START_IS_LATER_THEN_END = "Дата начала не может быть позднее даты окончания";

    private final BookingMapper bookingMapper;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final BookingRepository bookingRepository;


    @Override
    @Transactional
    public BookingResponseDto createNewBooking(BookingRequestDto bookingRequestDto, UserDTO userDTO, ItemDTO itemDTO) {
        if (!itemDTO.getAvailable()) {
            throw new BadRequestException(String.format(ITEM_IS_NOT_AVAILABLE, itemDTO.getId()));
        }

        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new BadRequestException(START_IS_LATER_THEN_END);
        }

        if (itemDTO.getOwner().getId().equals(userDTO.getId())) {
            throw new NotFoundException(USER_OWN_ITEM);
        }

        Booking booking = bookingMapper.toBooking(bookingRequestDto);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(userMapper.toUser(userDTO));
        booking.setItem(itemMapper.toItem(itemDTO, userMapper.toUser(itemDTO.getOwner())));

        Booking newBooking = bookingRepository.save(booking);

        return mapBookingToDTO(newBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto updateBooking(
        long bookingId, boolean approved, UserDTO userDTO
    ) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException(String.format(BOOKING_NOT_FOUND, bookingId));
        });

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BadRequestException(String.format(STATUS_ALREADY_CHANGED, bookingId));
        }

        if (booking.getBooker().getId().equals(userDTO.getId())) {
            throw new NotFoundException(NOT_AUTHORIZED);
        }

        if (!userDTO.getId().equals(booking.getItem().getOwner().getId())) {
            throw new BadRequestException(NOT_AUTHORIZED);
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return mapBookingToDTO(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getBookingById(Long bookingId, UserDTO userDTO) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException(String.format(BOOKING_NOT_FOUND, bookingId));
        });

        if (booking.getBooker().getId().equals(userDTO.getId()) ||
            booking.getItem().getOwner().getId().equals(userDTO.getId())
        ) {
            return mapBookingToDTO(booking);
        }

        throw new NotFoundException(String.format(BOOKING_NOT_FOUND, bookingId));
    }

    @Override
    public List<BookingResponseDto> getCurrentUserBookings(BookingState state, UserDTO userDTO) {
        long userId = userDTO.getId();
        LocalDateTime currentTime = LocalDateTime.now();

        List<Booking> bookings;

        switch (state) {
            case CURRENT:
                bookings =
                    bookingRepository.findCurrentBookingsByBookerId(userId, currentTime);
                break;
            case PAST:
                bookings = bookingRepository.findPastBookingsByBookerId(userId, currentTime);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookingsByBookerId(userId, currentTime);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByBookerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByBookerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
            case ALL:
            default:
                bookings = bookingRepository.findAllBookingsByBookerId(userId);
        }

        return mapBookingToDTO(bookings);
    }

    @Override
    public List<BookingResponseDto> getCurrentUserAllItemsBookings(BookingState state, UserDTO userDTO) {
        long ownerId = userDTO.getId();
        LocalDateTime currentTime = LocalDateTime.now();

        List<Booking> bookings;

        switch (state) {
            case CURRENT:
                bookings =
                    bookingRepository.findCurrentBookingsByOwnerId(ownerId, currentTime);
                break;
            case PAST:
                bookings = bookingRepository.findPastBookingsByOwnerId(ownerId, currentTime);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookingsByOwnerId(ownerId, currentTime);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByOwnerIdAndStatus(ownerId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED);
                break;
            case ALL:
            default:
                bookings = bookingRepository.findAllBookingsByOwnerId(ownerId);
        }

        return mapBookingToDTO(bookings);
    }

    private BookingResponseDto mapBookingToDTO(Booking booking) {
        return bookingMapper.toResponseDto(
            booking,
            userMapper.toDto(booking.getBooker()),
            itemMapper.toDto(booking.getItem(), userMapper.toDto(booking.getItem().getOwner()))
        );
    }

    private List<BookingResponseDto> mapBookingToDTO(List<Booking> bookings) {
        return bookings.stream().map(this::mapBookingToDTO).collect(Collectors.toList());
    }
}
