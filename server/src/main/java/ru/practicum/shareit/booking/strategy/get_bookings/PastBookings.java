package ru.practicum.shareit.booking.strategy.get_bookings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.constants.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.strategy.BookingParams;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PastBookings implements BookingsSearch {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> search(BookingParams params) {
        return bookingRepository.findPastBookingsByBookerId(
            params.getUserId(), LocalDateTime.now(), params.getPageRequest());
    }

    @Override
    public BookingState getState() {
        return BookingState.PAST;
    }
}