package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void getAllOwnRequests_whenUserIsFound_thenReturnedListOfItemRequests() {
        long userId = 1L;
        long requestId = 1L;
        long itemId = 1L;
        User user = User.builder().id(userId).build();
        ItemRequest itemRequest = ItemRequest.builder().id(requestId).requestor(user).build();
        Item item = Item.builder().id(itemId).owner(user).request(itemRequest).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorIdOrderByCreatedAsc(userId))
            .thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestIdInOrderByRequestCreatedAsc(anySet())).thenReturn(List.of(item));

        List<ItemRequestResponseDto> requests = itemRequestService.getAllOwnRequests(userId);

        assertEquals(
            List.of(
                ItemRequestMapper
                    .toResponseDto(
                        itemRequest,
                        UserMapper.toDto(user),
                        List.of(ItemMapper.toResponseDto(item, UserMapper.toDto(user)))
                    )
            ),
            requests
        );
    }

    @Test
    void getAllOwnRequests_whenUserIsNotFound_thenNotFoundThrown() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getAllOwnRequests(userId));
        verify(itemRequestRepository, never()).findByRequestorIdOrderByCreatedAsc(userId);
    }

    @Test
    void createNewRequest_whenUserIsFound_thenReturnedItemRequest() {
        long userId = 1L;
        User user = User.builder().build();
        ItemRequestRequestDto itemRequestRequestDto = ItemRequestRequestDto.builder().build();
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestRequestDto);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestResponseDto newRequest = itemRequestService.createNewRequest(itemRequestRequestDto, userId);
        newRequest.setCreated(null);

        assertEquals(ItemRequestMapper.toResponseDto(itemRequest, UserMapper.toDto(user), List.of()), newRequest);
    }


    @Test
    void getAllRequests_whenUserIsFound_thenReturnedListOfItemRequests() {
        long userId = 1L;
        long requestId = 1L;
        long itemId = 1L;
        User user = User.builder().id(userId).build();
        ItemRequest itemRequest = ItemRequest.builder().id(requestId).requestor(user).build();
        Item item = Item.builder().id(itemId).owner(user).request(itemRequest).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorIdNot(anyLong(), any(PageRequest.class))).thenReturn(
            List.of(itemRequest)
        );
        when(itemRepository.findByRequestIdInOrderByRequestCreatedAsc(anySet())).thenReturn(
            List.of(item)
        );

        List<ItemRequestResponseDto> requests = itemRequestService.getAllRequests(PageRequest.of(0, 10), userId);

        assertEquals(
            List.of(ItemRequestMapper
                .toResponseDto(
                    itemRequest,
                    UserMapper.toDto(user),
                    List.of(ItemMapper.toResponseDto(item, UserMapper.toDto(user)))
                )
            ),
            requests
        );
    }

    @Test
    void getRequestById_whenUserAndRequestIsFound_thenReturnedItemRequest() {
        long userId = 1L;
        long requestId = 1L;
        long itemId = 1L;
        User user = User.builder().id(userId).build();
        ItemRequest itemRequest = ItemRequest.builder().id(requestId).requestor(user).build();
        Item item = Item.builder().id(itemId).owner(user).request(itemRequest).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestIdOrderByRequestCreatedAsc(anyLong())).thenReturn(
            List.of(item)
        );
        ItemRequestResponseDto request = itemRequestService.getRequestById(requestId, userId);

        assertEquals(
            ItemRequestMapper.toResponseDto(
                itemRequest,
                UserMapper.toDto(user),
                List.of(ItemMapper.toResponseDto(item, UserMapper.toDto(user)))
            ),
            request
        );
    }

    @Test
    void getRequestById_whenRequestIsNotFound_thenBadRequestThrown() {
        long userId = 1L;
        long requestId = 1L;
        User user = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(requestId, userId));
    }
}