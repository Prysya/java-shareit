package ru.practicum.shareit.exception;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping(path = "/exception")
public class ExceptionController {
    @GetMapping("/400")
    public void getBadRequestException() {
        throw new BadRequestException("Bad request");
    }

    @GetMapping("/409")
    public void getConflictException() {
        throw new ConflictException("Conflict");
    }

    @GetMapping("/exception/throw")
    public void getException() throws Exception {
        throw new Exception("error");
    }
}