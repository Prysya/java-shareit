package ru.practicum.shareit.common;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

class AppPageRequestTest {

    @Test
    void getOffset() {
        PageRequest pageRequest = AppPageRequest.of(0, 1);

        assertEquals(0, pageRequest.getOffset());
    }
}