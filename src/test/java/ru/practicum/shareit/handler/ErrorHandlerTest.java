package ru.practicum.shareit.handler;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ExceptionController;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ExceptionController.class)
class ErrorHandlerTest {

    @Autowired
    private MockMvc mvc;


    @Test
    @SneakyThrows
    void handleBadRequest() {
        mvc.perform(get("/exception/400")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
            .andExpect(result -> assertEquals("Bad request",
                Objects.requireNonNull(result.getResolvedException()).getMessage()
            ));
    }

    @Test
    @SneakyThrows
    void handleConflictRequest() {
        mvc.perform(get("/exception/409")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConflictException))
            .andExpect(
                result -> assertEquals("Conflict", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }
}