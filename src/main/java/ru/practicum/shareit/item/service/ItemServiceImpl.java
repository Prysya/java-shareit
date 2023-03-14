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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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

        return items.stream().map(item -> {
            ItemResponseDto itemResponseDto = ItemMapper.toResponseDto(item, userDTO);
            setBookingsToDTO(itemResponseDto, userDTO.getId());
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

        UserDTO ownerDTO = UserMapper.toDto(item.getOwner());
        ItemResponseDto itemResponseDto = ItemMapper.toResponseDto(item, ownerDTO);
        setBookingsToDTO(itemResponseDto, requestUserId);
        setCommentsToDTO(itemResponseDto);

        return itemResponseDto;
    }

    @Override
    @Transactional
    public ItemResponseDto updateItem(Long itemId, Long userId, ItemRequestDto itemResponseDto) {
        User user = checkAndReturnUser(userId);
        Item oldItem = checkAndReturnItem(itemId);

        checkOwner(userId, oldItem);

        Item updatedItem = Item.builder()
            .id(itemId)
            .owner(oldItem.getOwner())
            .request(oldItem.getRequest())
            .name(Objects.requireNonNullElse(itemResponseDto.getName(), oldItem.getName()))
            .description(Objects.requireNonNullElse(itemResponseDto.getDescription(), oldItem.getDescription()))
            .available(Objects.requireNonNullElse(itemResponseDto.getAvailable(), oldItem.getAvailable()))
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
    public List<ItemResponseDto> searchAvailableItemsByText(String text, PageRequest pageRequest) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.findItemsWithText(text, pageRequest).stream().map(item -> {
            UserDTO ownerDTO = UserMapper.toDto(item.getOwner());

            ItemResponseDto itemResponseDto = ItemMapper.toResponseDto(item, ownerDTO);
            setBookingsToDTO(itemResponseDto, item.getOwner().getId());

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

    private void setBookingsToDTO(ItemResponseDto itemResponseDto, Long requestUserId) {
        if (itemResponseDto.getOwner().getId().equals(requestUserId)) {
            List<Booking> bookings = bookingRepository.findByItemIdOrderByIdDesc(itemResponseDto.getId());

            Booking lastBooking =
                bookings.stream().filter(booking -> !booking.getStatus().equals(BookingStatus.REJECTED))
                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now())).findFirst().orElse(null);

            Booking nextBooking =
                bookings.stream().filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now())).findFirst().orElse(null);

            itemResponseDto.setLastBooking(Objects.isNull(lastBooking) ? null :
                BookingMapper.toItemResponseDto(lastBooking, UserMapper.toDto(lastBooking.getBooker())));
            itemResponseDto.setNextBooking(Objects.isNull(nextBooking) ? null :
                BookingMapper.toItemResponseDto(nextBooking, UserMapper.toDto(nextBooking.getBooker())));
        }
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