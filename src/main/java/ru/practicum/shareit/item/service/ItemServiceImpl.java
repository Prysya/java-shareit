package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;


    @Override
    public List<ItemDTO> getAllItems(Long userId) {
        UserDTO userDTO = userMapper.toDto(checkAndReturnUser(userId));
        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(userId);

        return items.stream().map(item -> {
            ItemDTO itemDTO = itemMapper.toDto(item, userDTO);
            setBookingsToDTO(itemDTO, userDTO.getId());
            setCommentsToDTO(itemDTO);

            return itemDTO;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDTO saveItem(Long userId, ItemDTO itemDTO) {
        User user = checkAndReturnUser(userId);
        Item item = itemMapper.toItem(itemDTO, user);

        return itemMapper.toDto(itemRepository.save(item), userMapper.toDto(user));
    }

    @Override
    public ItemDTO getItemById(Long itemId, Long requestUserId) {
        Item item = checkAndReturnItem(itemId);

        UserDTO ownerDTO = userMapper.toDto(item.getOwner());
        ItemDTO itemDTO = itemMapper.toDto(item, ownerDTO);
        setBookingsToDTO(itemDTO, requestUserId);
        setCommentsToDTO(itemDTO);

        return itemDTO;
    }

    @Override
    @Transactional
    public ItemDTO updateItem(Long itemId, Long userId, ItemDTO itemDTO) {
        User user = checkAndReturnUser(userId);
        Item oldItem = checkAndReturnItem(itemId);

        checkOwner(userId, oldItem);

        Item updatedItem = Item.builder()
            .id(itemId)
            .owner(oldItem.getOwner())
            .request(oldItem.getRequest())
            .name(Objects.requireNonNullElse(itemDTO.getName(), oldItem.getName()))
            .description(Objects.requireNonNullElse(itemDTO.getDescription(), oldItem.getDescription()))
            .available(Objects.requireNonNullElse(itemDTO.getAvailable(), oldItem.getAvailable()))
            .build();

        itemRepository.save(updatedItem);

        return itemMapper.toDto(updatedItem, userMapper.toDto(user));
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
    public List<ItemDTO> searchAvailableItemsByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.findItemsWithText(text).stream().map(item -> {
            UserDTO ownerDTO = userMapper.toDto(item.getOwner());

            ItemDTO itemDTO = itemMapper.toDto(item, ownerDTO);
            setBookingsToDTO(itemDTO, item.getOwner().getId());

            return itemDTO;
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

        Comment comment = commentMapper.toComment(commentRequestDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        return commentMapper.toResponseDto(commentRepository.save(comment), userMapper.toDto(user));
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

    private void setCommentsToDTO(ItemDTO itemDTO) {
        List<Comment> comments = commentRepository.findByItemId(itemDTO.getId());

        itemDTO.setComments(comments.stream()
            .map(comment -> commentMapper.toResponseDto(comment, userMapper.toDto(comment.getAuthor())))
            .collect(Collectors.toList()));
    }

    private void setBookingsToDTO(ItemDTO itemDTO, Long requestUserId) {
        if (itemDTO.getOwner().getId().equals(requestUserId)) {
            List<Booking> bookings = bookingRepository.findByItemIdOrderByIdDesc(itemDTO.getId());


            Booking lastBooking =
                bookings.stream().filter(booking -> !booking.getStatus().equals(BookingStatus.REJECTED))
                    .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now())).findFirst().orElse(null);

            Booking nextBooking =
                bookings.stream().filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now())).findFirst().orElse(null);

            itemDTO.setLastBooking(Objects.isNull(lastBooking) ? null :
                bookingMapper.toItemResponseDto(lastBooking, userMapper.toDto(lastBooking.getBooker())));
            itemDTO.setNextBooking(Objects.isNull(nextBooking) ? null :
                bookingMapper.toItemResponseDto(nextBooking, userMapper.toDto(nextBooking.getBooker())));
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
}