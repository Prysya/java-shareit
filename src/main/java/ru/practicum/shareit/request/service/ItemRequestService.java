package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {
    List<ItemRequestResponseDto> getAllOwnRequests(long userId);

    ItemRequestResponseDto createNewRequest(ItemRequestRequestDto itemRequestRequestDto, long userId);

    List<ItemRequestResponseDto> getAllRequests(PageRequest pageRequest, long userId);

    ItemRequestResponseDto getRequestById(long requestId, long userId);
}
