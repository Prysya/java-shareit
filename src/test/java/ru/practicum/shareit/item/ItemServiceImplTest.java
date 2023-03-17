package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.constants.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.common.AppPageRequest;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    private final AppPageRequest defaultAppPageRequest = new AppPageRequest(0, 10);
    private final User basicUser = User.builder().id(1L).name("name").build();
    private final Item basicItem = Item.builder().id(1L).owner(basicUser).build();
    private final ItemResponseDto basicItemResponse = ItemMapper.toResponseDto(basicItem, UserMapper.toDto(basicUser));
    private final ItemRequestDto basicItemRequest = ItemRequestDto.builder().build();
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    /**
     * getAllItems
     */
    @Test
    void getAllItems_whenAllDataIsCorrect_thenReturnListOfItems() {
        long userId = 2L;
        List<Item> itemsList = List.of(basicItem);

        when(userRepository.findById(userId)).thenReturn(Optional.of(basicUser));
        when(itemRepository.findByOwnerIdOrderByIdAsc(eq(userId), any(AppPageRequest.class))).thenReturn(itemsList);

        List<ItemResponseDto> items = itemService.getAllItems(userId, defaultAppPageRequest);
        assertEquals(1, items.size());
    }

    @Test
    void getAllItems_whenUserIsNotFound_thenNotFoundThrows() {
        long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getAllItems(userId, defaultAppPageRequest));

        verify(itemRepository, never()).findByOwnerIdOrderByIdAsc(eq(userId), any(AppPageRequest.class));
    }

    /**
     * saveItem
     */
    @Test
    void saveItem_whenUserIsFound_thenReturnItem() {
        long userId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(basicUser));
        when(itemRepository.save(any(Item.class))).thenReturn(basicItem);

        ItemResponseDto itemResponseDto = itemService.saveItem(userId, basicItemRequest);

        assertEquals(basicItemResponse, itemResponseDto);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void saveItem_whenUserIsFoundAndItemRequestIsProvided_thenReturnItem() {
        long userId = 2L;
        long requestId = 1L;
        ItemRequest itemRequest = ItemRequest.builder().id(requestId).build();
        ItemRequestDto itemRequestDtoWithRequestId = ItemRequestDto.builder().requestId(itemRequest.getId()).build();
        Item itemWithItemRequest = Item.builder().owner(basicUser).request(itemRequest).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(basicUser));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(itemWithItemRequest);

        ItemResponseDto itemResponseDto = itemService.saveItem(userId, itemRequestDtoWithRequestId);

        assertEquals(ItemMapper.toResponseDto(itemWithItemRequest, UserMapper.toDto(basicUser)), itemResponseDto);
        assertEquals(requestId, itemResponseDto.getRequestId());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void saveItem_whenUserIsFoundAndItemRequestIsProvidedButNotFound_thenNotFoundThrown() {
        long userId = 2L;
        long requestId = 1L;
        ItemRequest itemRequest = ItemRequest.builder().id(requestId).build();
        ItemRequestDto itemRequestDtoWithRequestId = ItemRequestDto.builder().requestId(itemRequest.getId()).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(basicUser));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.saveItem(userId, itemRequestDtoWithRequestId));

        verify(itemRepository, never()).save(any(Item.class));
    }

    /**
     * getItemById
     */
    @Test
    void getItemById_whenItemIsFound_thenReturnItem() {
        long itemId = 1L;
        long userId = basicUser.getId();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(basicItem));

        ItemResponseDto item = itemService.getItemById(itemId, userId);

        assertEquals(basicItemResponse, item);
    }

    @Test
    void getItemById_whenItemIsFoundWithComments_thenReturnItem() {
        long itemId = 1L;
        long userId = basicUser.getId();
        Comment comment = Comment.builder().author(basicUser).build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(basicItem));
        when(commentRepository.findByItemId(itemId)).thenReturn(List.of(
            comment
        ));

        ItemResponseDto item = itemService.getItemById(itemId, userId);

        assertEquals(
            ItemResponseDto.builder()
                .id(itemId)
                .owner(UserMapper.toDto(basicUser))
                .comments(List.of(CommentMapper.toResponseDto(comment, UserMapper.toDto(comment.getAuthor())))).build(),
            item
        );
    }

    @Test
    void getItemById_whenItemIsFoundButUserIsNotOwner_thenReturnItemWithoutBookings() {
        long itemId = 1L;
        long userId = 1L;

        Item itemWithAnotherOwner = Item.builder().id(1L).owner(User.builder().id(2L).build()).build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemWithAnotherOwner));

        ItemResponseDto item = itemService.getItemById(itemId, userId);

        assertEquals(
            ItemMapper.toResponseDto(itemWithAnotherOwner, UserMapper.toDto(itemWithAnotherOwner.getOwner())), item);
    }

    @Test
    void getItemById_whenItemIsFoundWithBookings_thenReturnItemWithoutBookings() {
        long itemId = 1L;
        long userId = 1L;
        User booker = User.builder().id(3L).build();
        Item item = Item.builder().id(itemId).build();
        List<Booking> lastBookings = List.of(
            Booking.builder().id(1L).item(item).status(BookingStatus.APPROVED).booker(booker)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2)).build()
        );

        List<Booking> nextBookings = List.of(
            Booking.builder().id(1L).item(item).status(BookingStatus.APPROVED).booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2)).build()
        );

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(basicItem));
        when(
            bookingRepository.findByItemIdAndOwnerIdAndStartDateLessThenNowInOrderByIdDesc(anySet(), anyLong(),
                any(LocalDateTime.class)
            )).thenReturn(lastBookings);
        when(bookingRepository.findByItemIdAndOwnerIdAndStartDateIsMoreThenNowInOrderByIdAsc(
            anySet(),
            anyLong(),
            any(LocalDateTime.class)
        )).thenReturn(nextBookings);

        ItemResponseDto itemResponse = itemService.getItemById(itemId, userId);

        assertEquals(
            BookingMapper.toItemResponseDto(nextBookings.get(0), UserMapper.toDto(booker)),
            itemResponse.getNextBooking()
        );
        assertEquals(
            BookingMapper.toItemResponseDto(lastBookings.get(0), UserMapper.toDto(booker)),
            itemResponse.getLastBooking()
        );
    }


    @Test
    void getItemById_whenItemIsNotFound_thenNotFoundThrown() {
        long itemId = 1L;
        long userId = basicUser.getId();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(itemId, userId));
    }

    /**
     * updateItem
     */
    @Test
    void updateItem_whenUserAndItemIsFoundAndUserIdEqualsOwnerId_thenReturnUpdatedItem() {
        long userId = basicUser.getId();
        long itemId = basicItem.getId();

        String newName = "newName";
        String newDesc = "newDesc";
        Boolean newAvailable = true;

        ItemRequestDto newItem =
            ItemRequestDto.builder().name(newName).description(newDesc).available(newAvailable).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(basicUser));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(basicItem));

        ItemResponseDto itemResponseDto = itemService.updateItem(itemId, userId, newItem);

        assertEquals(newName, itemResponseDto.getName());
        assertEquals(newDesc, itemResponseDto.getDescription());
        assertEquals(newAvailable, itemResponseDto.getAvailable());
        assertNotEquals(basicItem.getName(), itemResponseDto.getName());
        assertNotEquals(basicItem.getDescription(), itemResponseDto.getDescription());
        assertNotEquals(basicItem.getAvailable(), itemResponseDto.getAvailable());
    }

    @Test
    void updateItem_whenUserAndItemIsFoundButUserIdNotEqualsOwnerId_thenNotFoundThrown() {
        long userId = 3L;
        long itemId = basicItem.getId();

        String newName = "newName";
        String newDesc = "newDesc";
        Boolean newAvailable = true;

        ItemRequestDto newItem =
            ItemRequestDto.builder().name(newName).description(newDesc).available(newAvailable).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(basicUser));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(basicItem));

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemId, userId, newItem));

        verify(itemRepository, never()).save(any(Item.class));
    }

    /**
     * deleteItem
     */
    @Test
    void deleteItem_whenUserAndItemIsFound_thenShouldInvokeWithoutErrors() {
        long userId = basicUser.getId();
        long itemId = basicItem.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(basicUser));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(basicItem));

        itemService.deleteItem(userId, itemId);

        verify(itemRepository).deleteById(itemId);
    }

    /**
     * searchAvailableItemsByText
     */
    @Test
    void searchAvailableItemsByText_whenTextIsBlank_thenReturnEmptyList() {
        long userId = basicUser.getId();
        List<ItemResponseDto> list = itemService.searchAvailableItemsByText(userId, "", defaultAppPageRequest);

        assertTrue(list.isEmpty());
        verify(itemRepository, never()).findAvailableItemsWithText(anyString(), any(AppPageRequest.class));
    }

    @Test
    void searchAvailableItemsByText_whenTextIsNotBlank_thenReturnListOfItems() {
        String text = "text";
        long userId = basicUser.getId();

        when(itemRepository.findAvailableItemsWithText(text, defaultAppPageRequest)).thenReturn(List.of(basicItem));

        List<ItemResponseDto> list = itemService.searchAvailableItemsByText(userId, text, defaultAppPageRequest);

        assertEquals(1, list.size());
        assertEquals(basicItemResponse, list.get(0));
    }

    /**
     * addComment
     */
    @Test
    void addComment_whenUserAndItemIsFoundAndUserHasBookings_thenReturnComment() {
        long userId = basicUser.getId();
        long itemId = basicItem.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(basicUser));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(basicItem));
        when(bookingRepository.findByItemIdAndBookerIdAndEndLessThanAndStatus(eq(itemId), eq(userId),
            any(LocalDateTime.class), any(BookingStatus.class)
        )).thenReturn(List.of(Booking.builder().build()));
        when(commentRepository.save(any(Comment.class))).thenReturn(Comment.builder().build());

        CommentResponseDto responseDto = itemService.addComment(userId, itemId, CommentRequestDto.builder().build());

        assertEquals(basicUser.getName(), responseDto.getAuthorName());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addComment_whenUserAndItemIsFoundButUserNotBookingThisItem_thenBadRequestThrown() {
        long userId = basicUser.getId();
        long itemId = basicItem.getId();
        CommentRequestDto commentRequestDto = CommentRequestDto.builder().build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(basicUser));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(basicItem));
        when(bookingRepository.findByItemIdAndBookerIdAndEndLessThanAndStatus(eq(itemId), eq(userId),
            any(LocalDateTime.class), any(BookingStatus.class)
        )).thenReturn(List.of());

        assertThrows(BadRequestException.class, () -> itemService.addComment(userId, itemId, commentRequestDto));
        verify(commentRepository, never()).save(any(Comment.class));
    }
}