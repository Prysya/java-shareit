package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {
    private final User user = User.builder().name("name").email("email").id(1L).build();
    private final User owner = User.builder().id(2L).name("name2").email("email2").build();
    private final ItemRequest itemRequest =
        ItemRequest.builder().created(LocalDateTime.now()).description("desc").requestor(owner).build();
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    @BeforeEach
    public void createUsers() {
        userRepository.save(user);
        userRepository.save(owner);
        itemRequestRepository.save(itemRequest);
    }


    @AfterEach
    public void clearItemRepo() {
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByRequestIdOrderByRequestCreatedAsc() {
        Long requestId = 1L;

        itemRepository.saveAll(List.of(
            Item.builder().name("test").description("desc").available(false).owner(owner).request(itemRequest).build(),
            Item.builder().name("test1").description("desc").available(false).owner(owner).build()
        ));

        List<Item> items =
            itemRepository.findByRequestIdOrderByRequestCreatedAsc(requestId);

        assertEquals(1, items.size());
        assertEquals("test", items.get(0).getName());
    }

    @Test
    void findByOwnerIdOrderByIdAsc() {
        itemRepository.saveAll(List.of(
            Item.builder().owner(user).name("test").description("desc").available(false).build(),
            Item.builder().owner(owner).name("test").description("desc").available(false).build(),
            Item.builder().owner(owner).name("test").description("desc").available(false).build()
        ));

        List<Item> items =
            itemRepository.findByOwnerIdOrderByIdAsc(owner.getId(), Pageable.unpaged());

        assertEquals(2, items.size());
    }

    @Test
    void findAvailableItemsWithText() {
        itemRepository.saveAll(List.of(
            Item.builder().name("1 test 1").owner(owner).description("desc").available(true).build(),
            Item.builder().name("name").owner(owner).description("test").available(true).build(),
            Item.builder().name("name").owner(owner).description("tets").available(true).build()
        ));

        List<Item> items = itemRepository.findAll();
        List<Item> itemsByText =
            itemRepository.findAvailableItemsWithText("test", Pageable.unpaged());

        assertEquals(3, items.size());
        assertEquals(2, itemsByText.size());
    }
}