package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.constants.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b where b.booker.id = ?1 order by b.start DESC")
    List<Booking> findAllBookingsByBookerId(Long bookerId, Pageable pageable);

    @Query("select b from Booking b " +
        "where b.booker.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start DESC")
    List<Booking> findCurrentBookingsByBookerId(
        Long bookerId, LocalDateTime currentDate, Pageable pageable
    );

    @Query("select b from Booking b " +
        "where b.booker.id = ?1 and b.end < ?2 order by b.start DESC")
    List<Booking> findPastBookingsByBookerId(
        Long bookerId, LocalDateTime currentDate, Pageable pageable
    );

    @Query("select b from Booking b " +
        "where b.booker.id = ?1 and b.start > ?2 order by b.start DESC")
    List<Booking> findFutureBookingsByBookerId(
        Long bookerId, LocalDateTime currentDate, Pageable pageable
    );

    @Query("select b from Booking b " +
        "where b.booker.id = ?1 and b.status = ?2 order by b.start DESC")
    List<Booking> findBookingsByBookerIdAndStatus(
        Long bookerId, BookingStatus status, Pageable pageable
    );

    @Query("select b from Booking b where b.item.owner.id = ?1 order by b.start DESC")
    List<Booking> findAllBookingsByOwnerId(Long ownerId, Pageable pageable);

    @Query("select b from Booking b " +
        "where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start DESC")
    List<Booking> findCurrentBookingsByOwnerId(
        Long ownerId, LocalDateTime currentDate, Pageable pageable
    );


    @Query("select b from Booking b " +
        "where b.item.owner.id = ?1 and b.end < ?2 order by b.start DESC")
    List<Booking> findPastBookingsByOwnerId(
        Long ownerId, LocalDateTime currentDate, Pageable pageable
    );

    @Query("select b from Booking b " +
        "where b.item.owner.id = ?1 and b.start > ?2 order by b.start DESC")
    List<Booking> findFutureBookingsByOwnerId(
        Long ownerId, LocalDateTime currentDate, Pageable pageable
    );

    @Query("select b from Booking b " +
        "where b.item.owner.id = ?1 and b.status = ?2 order by b.start DESC")
    List<Booking> findBookingsByOwnerIdAndStatus(
        Long ownerId, BookingStatus status, Pageable pageable
    );

    List<Booking> findByItemIdAndBookerIdAndEndLessThanAndStatus(
        Long id, Long id1, LocalDateTime end, BookingStatus status
    );

    List<Booking> findByItemIdOrderByIdDesc(Long id);
}