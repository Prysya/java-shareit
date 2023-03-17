package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.constants.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.constants.ItemErrorMessage;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.constants.ItemRequestErrorMessage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.constants.UserErrorMessage;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public
class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;


    @Override
    public List<ItemResponseDto> getAllItems(Long userId, PageRequest pageRequest) {
        UserDTO userDTO = UserMapper.toDto(checkAndReturnUser(userId));
        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(userId, pageRequest);

        Set<Long> ids = items.stream().map(Item::getId).collect(Collectors.toSet());
        Map<Long, List<Booking>> lastBookings = getAllLastBookingsByItemId(ids, userDTO.getId());
        Map<Long, List<Booking>> nextBookings = getAllNextBookingsByItemId(ids, userDTO.getId());

        return items.stream().map(item -> {
            ItemResponseDto itemResponseDto = ItemMapper.toResponseDto(item, userDTO);
            setBookingsToDTO(
                Optional.ofNullable(lastBookings.get(item.getId())),
                Optional.ofNullable(nextBookings.get(item.getId())),
                itemResponseDto
            );
            setCommentsToDTO(itemResponseDto);

            return itemResponseDto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemResponseDto saveItem(Long userId, ItemRequestDto itemRequestDto) {
        User user = checkAndReturnUser(userId);
        Item item = ItemMapper.toItem(itemRequestDto, user);

        if (Objects.nonNull(itemRequestDto.getRequestId())) {
            ItemRequest request = checkAndReturnItemRequest(itemRequestDto.getRequestId());
            item.setRequest(request);
        }

        return ItemMapper.toResponseDto(itemRepository.save(item), UserMapper.toDto(user));
    }

    @Override
    public ItemResponseDto getItemById(Long itemId, Long requestUserId) {
        Item item = checkAndReturnItem(itemId);


        Map<Long, List<Booking>> lastBookings = getAllLastBookingsByItemId(Set.of(itemId), requestUserId);
        Map<Long, List<Booking>> nextBookings = getAllNextBookingsByItemId(Set.of(itemId), requestUserId);

        UserDTO ownerDTO = UserMapper.toDto(item.getOwner());
        ItemResponseDto itemResponseDto = ItemMapper.toResponseDto(item, ownerDTO);
        setBookingsToDTO(
            Optional.ofNullable(lastBookings.get(item.getId())),
            Optional.ofNullable(nextBookings.get(item.getId())),
            itemResponseDto
        );
        setCommentsToDTO(itemResponseDto);

        return itemResponseDto;
    }

    @Override
    @Transactional
    public ItemResponseDto updateItem(Long itemId, Long userId, ItemRequestDto itemRequestDto) {
        User user = checkAndReturnUser(userId);
        Item oldItem = checkAndReturnItem(itemId);

        checkOwner(userId, oldItem);

        Item updatedItem = Item.builder()
            .id(itemId)
            .owner(oldItem.getOwner())
            .request(oldItem.getRequest())
            .name(Objects.requireNonNullElse(itemRequestDto.getName(), oldItem.getName()))
            .description(Objects.requireNonNullElse(itemRequestDto.getDescription(), oldItem.getDescription()))
            .available(Objects.requireNonNullElse(itemRequestDto.getAvailable(), oldItem.getAvailable()))
            .build();

        itemRepository.save(updatedItem);

        return ItemMapper.toResponseDto(updatedItem, UserMapper.toDto(user));
    }

    @Override
    @Transactional
    public void deleteItem(Long userId, Long itemId) {
        checkAndReturnUser(userId);
        Item item = checkAndReturnItem(itemId);


        checkOwner(userId, item);

        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemResponseDto> searchAvailableItemsByText(Long userId, String text, PageRequest pageRequest) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        List<Item> items = itemRepository.findAvailableItemsWithText(text, pageRequest);

        Set<Long> ids = items.stream().map(Item::getId).collect(Collectors.toSet());
        Map<Long, List<Booking>> lastBookings = getAllLastBookingsByItemId(ids, userId);
        Map<Long, List<Booking>> nextBookings = getAllNextBookingsByItemId(ids, userId);

        return items.stream().map(item -> {
            UserDTO ownerDTO = UserMapper.toDto(item.getOwner());

            ItemResponseDto itemResponseDto = ItemMapper.toResponseDto(item, ownerDTO);
            setBookingsToDTO(
                Optional.ofNullable(lastBookings.get(item.getId())),
                Optional.ofNullable(nextBookings.get(item.getId())),
                itemResponseDto
            );

            return itemResponseDto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto) {
        User user = checkAndReturnUser(userId);
        Item item = checkAndReturnItem(itemId);

        List<Booking> bookings =
            bookingRepository.findByItemIdAndBookerIdAndEndLessThanAndStatus(itemId, userId,
                LocalDateTime.now(), BookingStatus.APPROVED
            );


        if (bookings.isEmpty()) {
            throw new BadRequestException(ItemErrorMessage.COMMENT_ERROR);
        }

        Comment comment = CommentMapper.toComment(commentRequestDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.toResponseDto(commentRepository.save(comment), UserMapper.toDto(user));
    }

    /**
     * Проверка, что вещь принадлежит текущему пользователю
     *
     * @param userId уникальный идентификатор текущего пользователя
     * @param item   {@link Item}
     */
    private void checkOwner(Long userId, Item item) {
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException(String.format(ItemErrorMessage.UNAUTHORIZED, item.getId(), userId));
        }
    }

    private void setCommentsToDTO(ItemResponseDto itemResponseDto) {
        List<Comment> comments = commentRepository.findByItemId(itemResponseDto.getId());

        itemResponseDto.setComments(comments.stream()
            .map(comment -> CommentMapper.toResponseDto(comment, UserMapper.toDto(comment.getAuthor())))
            .collect(Collectors.toList()));
    }

    private void setBookingsToDTO(
        Optional<List<Booking>> lastBookings, Optional<List<Booking>> nextBookings, ItemResponseDto itemResponseDto
    ) {

        itemResponseDto
            .setLastBooking(lastBookings.flatMap(bookings -> Optional.ofNullable(bookings.get(0))
                    .map(value -> BookingMapper.toItemResponseDto(value, UserMapper.toDto(value.getBooker()))))
                .orElse(null));

        itemResponseDto
            .setNextBooking(nextBookings.flatMap(bookings -> Optional.ofNullable(bookings.get(0))
                    .map(value -> BookingMapper.toItemResponseDto(value, UserMapper.toDto(value.getBooker()))))
                .orElse(null));
    }

    private Map<Long, List<Booking>> getAllLastBookingsByItemId(Set<Long> itemIds, Long userId) {
        return bookingRepository.findByItemIdAndOwnerIdAndStartDateLessThenNowInOrderByIdDesc(
                itemIds, userId, LocalDateTime.now()).stream()
            .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
    }

    private Map<Long, List<Booking>> getAllNextBookingsByItemId(Set<Long> itemIds, Long userId) {
        return bookingRepository.findByItemIdAndOwnerIdAndStartDateIsMoreThenNowInOrderByIdAsc(
                itemIds, userId, LocalDateTime.now())
            .stream()
            .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
    }

    private User checkAndReturnUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format(UserErrorMessage.NOT_FOUND, userId)));
    }

    private Item checkAndReturnItem(Long itemId) {
        return itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException(String.format(ItemErrorMessage.NOT_FOUND, itemId)));
    }

    private ItemRequest checkAndReturnItemRequest(Long requestId) {
        return itemRequestRepository.findById(requestId)
            .orElseThrow(() -> new NotFoundException(ItemRequestErrorMessage.NOT_FOUND));
    }
}