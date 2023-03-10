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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemRequestResponseDto> getAllOwnRequests(long userId) {
        User user = checkAndReturnUser(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdOrderByCreatedAsc(userId);

        return itemRequests.stream().map(
            itemRequest -> itemRequestMapper.toResponseDto(itemRequest, userMapper.toDto(user),
                getItems(itemRequest.getId())
            )).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemRequestResponseDto createNewRequest(ItemRequestRequestDto itemRequestRequestDto, long userId) {
        User user = checkAndReturnUser(userId);
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestRequestDto);

        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);

        return itemRequestMapper.toResponseDto(
            itemRequestRepository.save(itemRequest), userMapper.toDto(user), new ArrayList<>());
    }

    @Override
    public List<ItemRequestResponseDto> getAllRequests(PageRequest pageRequest, long userId) {
        checkAndReturnUser(userId);

        return itemRequestRepository.findByRequestorIdNot(userId, pageRequest).stream().map(
            itemRequest -> itemRequestMapper.toResponseDto(itemRequestRepository.save(itemRequest),
                userMapper.toDto(itemRequest.getRequestor()), getItems(itemRequest.getId())
            )).collect(Collectors.toList());
    }

    @Override
    public ItemRequestResponseDto getRequestById(long requestId, long userId) {
        checkAndReturnUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
            .orElseThrow(() -> new NotFoundException(ItemRequestErrorMessage.NOT_FOUND));

        return itemRequestMapper.toResponseDto(
            itemRequest, userMapper.toDto(itemRequest.getRequestor()), getItems(requestId));
    }

    private User checkAndReturnUser(long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format(UserErrorMessage.NOT_FOUND, userId)));
    }

    private List<ItemResponseDto> getItems(long requestId) {
        return itemRepository.findByRequestIdOrderByRequestCreatedAsc(requestId).stream()
            .map(item -> itemMapper.toResponseDto(item, userMapper.toDto(item.getOwner())))
            .collect(Collectors.toList());
    }
}
