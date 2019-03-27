package com.example.repository;

import com.example.model.Location;
import com.example.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByOrderByDate();

    @Query("SELECT f FROM ORDERS f ORDER BY f.date DESC")
    List<Order> findAllByOrderByDateDsc();

    List<Order> findAllByEndDate(Date d);

    List<Order> findAllByEndDateOrderByDate(Date d);
}
