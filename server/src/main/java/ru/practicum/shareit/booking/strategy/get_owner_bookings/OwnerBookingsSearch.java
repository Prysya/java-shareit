package ru.practicum.shareit.booking.strategy.get_owner_bookings;

import ru.practicum.shareit.booking.constants.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.strategy.BookingParams;

import java.util.List;

public interface OwnerBookingsSearch {
    List<Booking> search(BookingParams params);

    BookingState getState();
}
