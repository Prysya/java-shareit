package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingRequestDtoJsonTest {

    @Autowired
    private JacksonTester<BookingRequestDto> json;

    @Test
    @SneakyThrows
    void testBookingRequestDto() {
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
            .start(LocalDateTime.now())
            .end(LocalDateTime.now())
            .available(false)
            .itemId(1L)
            .build();


        JsonContent<BookingRequestDto> result = json.write(bookingRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.start")
            .isEqualTo(bookingRequestDto.getStart().truncatedTo(ChronoUnit.SECONDS).toString());
        assertThat(result).extractingJsonPathStringValue("$.start")
            .matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$");
        assertThat(result).extractingJsonPathStringValue("$.end")
            .isEqualTo(bookingRequestDto.getEnd().truncatedTo(ChronoUnit.SECONDS).toString());
        assertThat(result).extractingJsonPathStringValue("$.start")
            .matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$");
        assertThat(result).extractingJsonPathBooleanValue("$.available")
            .isEqualTo(bookingRequestDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
            .isEqualTo(1);
    }
}