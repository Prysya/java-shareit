package ru.practicum.shareit.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppPageRequest)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AppPageRequest that = (AppPageRequest) o;
        return from == that.from;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), from);
    }
}
