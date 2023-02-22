package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class ItemServiceImpl implements ItemService {
    public static final String NOT_FOUND = "Вещь c id: '%d' не найдена";
    public static final String UNAUTHORIZED = "Вещь с id: '%d', не принадлежит пользователю с id: '%d";

    private final ItemRepository repository;
    private final ItemMapper itemMapper;

    private final UserService userService;


    @Override
    public List<ItemDTO> getAllItems(Long userId) {
        UserDTO userDTO = userService.getUserById(userId);

        return repository.getAllItems(userDTO.getId()).stream().map(item -> {
            ItemDTO itemDTO = itemMapper.toDto(item);
            itemDTO.setOwner(userDTO);
            return itemDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public ItemDTO saveItem(Long userId, ItemDTO itemDTO) {
        UserDTO userDTO = userService.getUserById(userId);

        ItemDTO newItemDTO = itemMapper.toDto(
            repository.saveItem(
                userId,
                itemMapper.toItem(itemDTO)
            )
        );

        newItemDTO.setOwner(userDTO);

        return newItemDTO;
    }

    @Override
    public ItemDTO getItemById(Long itemId) {
        Item item = repository.getItemById(itemId)
            .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND, itemId)));

        ItemDTO itemDTO = itemMapper.toDto(item);
        itemDTO.setOwner(userService.getUserById(item.getOwner()));

        return itemDTO;
    }

    @Override
    public ItemDTO updateItem(Long itemId, Long userId, ItemDTO itemDTO) {
        UserDTO userDTO = userService.getUserById(userId);
        Item item = repository.getItemById(itemId)
            .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND, itemId)));

        checkOwner(userId, item);

        ItemDTO updatedItemDTO = itemMapper.toDto(repository.updateItem(itemId, itemMapper.toItem(itemDTO)));
        updatedItemDTO.setOwner(userDTO);

        return updatedItemDTO;
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        userService.getUserById(userId);

        Item item = repository.getItemById(itemId)
            .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND, itemId)));


        checkOwner(userId, item);

        if (Boolean.FALSE.equals(repository.deleteItem(itemId))) {
            throw new NotFoundException(String.format(NOT_FOUND, itemId));
        }
    }

    @Override
    public List<ItemDTO> searchAvailableItemsByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return repository.searchAvailableItemsByText(text).stream().map(item -> {
            ItemDTO itemDTO = itemMapper.toDto(item);
            itemDTO.setOwner(userService.getUserById(item.getOwner()));
            return itemDTO;
        }).collect(Collectors.toList());
    }

    /**
     * Проверка, что вещь принадлежит текущему пользователю
     *
     * @param userId уникальный идентификатор текущего пользователя
     * @param item {@link Item}
     */
    private void checkOwner(Long userId, Item item) {
        if (!Objects.equals(item.getOwner(), userId)) {
            throw new NotFoundException(String.format(UNAUTHORIZED, item.getId(), userId));
        }
    }
}