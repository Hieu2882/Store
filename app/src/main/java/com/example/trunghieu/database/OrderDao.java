package com.example.trunghieu.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.trunghieu.models.Order;

import java.util.List;

@Dao
public interface OrderDao {
    @Insert
    long insert(Order order);

    @Update
    void update(Order order);

    @Delete
    void delete(Order order);

    @Query("SELECT * FROM orders")
    List<Order> getAllOrders();

    @Query("SELECT * FROM orders WHERE id = :id")
    Order getOrderById(int id);

    @Query("SELECT * FROM orders WHERE userId = :userId")
    List<Order> getOrdersByUserId(int userId);

    @Query("SELECT * FROM orders WHERE status = :status")
    List<Order> getOrdersByStatus(String status);

    @Query("SELECT * FROM orders WHERE userId = :userId AND status = :status")
    List<Order> getOrdersByUserIdAndStatus(int userId, String status);
} 