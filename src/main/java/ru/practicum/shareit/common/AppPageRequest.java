package ru.practicum.shareit.common;

import lombok.EqualsAndHashCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


@EqualsAndHashCode(callSuper = false)
public class AppPageRequest extends PageRequest {
    private final int from;

    public AppPageRequest(int from, int size) {
        super(from / size, size, Sort.unsorted());
        this.from = from;
    }

    @Override
    public long getOffset() {
        return from;
    }
}