package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerIdOrderByIdAsc(Long id);

    @Query("select i from Item i where (lower(i.name) like concat('%', lower(:text), '%') or lower(i.description) like concat('%', lower(:text), '%')) and i.available = true order by i.id")
    List<Item> findItemsWithText(@Param("text") String text);
}