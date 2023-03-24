package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.booking.validator.*;

import javax.validation.GroupSequence;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
@GroupSequence({BookingRequestDto.class, FirstOrder.class, SecondOrder.class})
@StartEqualsEnd(message = "Дата начала не может равна дате окончания", groups = SecondOrder.class)
@StartBeforeEnd(message = "Дата начала не может позднее даты окончания", groups = SecondOrder.class)
public class BookingRequestDto implements Serializable {
    @NotNull(message = "Дата начала не может быть пустой", groups = FirstOrder.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @FutureOrPresent(message = "Дата начала не может быть в прошлом", groups = SecondOrder.class)
    private LocalDateTime start;

    @NotNull(message = "Дата конца не может быть пустой", groups = FirstOrder.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @FutureOrPresent(message = "Дата конца не может быть в прошлом", groups = SecondOrder.class)
    private LocalDateTime end;
    @NotNull(groups = FirstOrder.class)
    private Long itemId;

    private Boolean available;
}