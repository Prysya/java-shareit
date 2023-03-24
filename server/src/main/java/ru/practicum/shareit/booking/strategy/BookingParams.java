package ru.practicum.shareit.booking.strategy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;


@Getter
@AllArgsConstructor
public class BookingParams {
    private long userId;
    private PageRequest pageRequest;
}
