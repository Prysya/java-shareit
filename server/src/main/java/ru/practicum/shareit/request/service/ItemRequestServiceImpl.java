package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.constants.ItemRequestErrorMessage;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.constants.UserErrorMessage;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Override
    public List<ItemRequestResponseDto> getAllOwnRequests(long userId) {
        User user = checkAndReturnUser(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdOrderByCreatedAsc(userId);

        Map<Long, List<ItemResponseDto>> itemsMap =
            getItems(itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toSet()));


        return itemRequests.stream().map(
            itemRequest -> ItemRequestMapper.toResponseDto(
                itemRequest,
                UserMapper.toDto(user),
                itemsMap.getOrDefault(itemRequest.getId(), List.of())
            )).collect(Collectors.toList()
        );
    }

    @Override
    @Transactional
    public ItemRequestResponseDto createNewRequest(ItemRequestRequestDto itemRequestRequestDto, long userId) {
        User user = checkAndReturnUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestRequestDto);

        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);

        return ItemRequestMapper.toResponseDto(
            itemRequestRepository.save(itemRequest), UserMapper.toDto(user), new ArrayList<>()
        );
    }

    @Override
    public List<ItemRequestResponseDto> getAllRequests(PageRequest pageRequest, long userId) {
        checkAndReturnUser(userId);

        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdNot(userId, pageRequest);

        Map<Long, List<ItemResponseDto>> itemsMap =
            getItems(itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toSet()));

        return itemRequests.stream().map(
            itemRequest -> ItemRequestMapper.toResponseDto(
                itemRequest,
                UserMapper.toDto(itemRequest.getRequestor()),
                itemsMap.get(itemRequest.getId())
            )
        ).collect(Collectors.toList());
    }

    @Override
    public ItemRequestResponseDto getRequestById(long requestId, long userId) {
        checkAndReturnUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
            .orElseThrow(() -> new NotFoundException(ItemRequestErrorMessage.NOT_FOUND));

        return ItemRequestMapper.toResponseDto(
            itemRequest, UserMapper.toDto(itemRequest.getRequestor()), getItems(requestId)
        );
    }

    private User checkAndReturnUser(long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format(UserErrorMessage.NOT_FOUND, userId)));
    }

    private List<ItemResponseDto> getItems(long requestId) {
        return itemRepository.findByRequestIdOrderByRequestCreatedAsc(requestId).stream()
            .map(item -> ItemMapper.toResponseDto(item, UserMapper.toDto(item.getOwner())))
            .collect(Collectors.toList());
    }

    private Map<Long, List<ItemResponseDto>> getItems(Set<Long> requestIds) {
        return itemRepository.findByRequestIdInOrderByRequestCreatedAsc(requestIds)
            .stream()
            .map(item -> ItemMapper.toResponseDto(item, UserMapper.toDto(item.getOwner())))
            .collect(Collectors.groupingBy(ItemResponseDto::getRequestId));
    }
}
