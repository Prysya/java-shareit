package ru.practicum.shareit.booking.validator;

import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartEqualsEndValidator implements ConstraintValidator<StartEqualsEnd, BookingRequestDto> {
    @Override
    public boolean isValid(BookingRequestDto bookingRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        return !bookingRequestDto.getStart().isEqual(bookingRequestDto.getEnd());
    }
}