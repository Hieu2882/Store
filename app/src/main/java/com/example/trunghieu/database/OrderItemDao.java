package com.example.trunghieu.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.trunghieu.models.OrderItem;

import java.util.List;

@Dao
public interface OrderItemDao {
    @Insert
    void insert(OrderItem orderItem);

    @Update
    void update(OrderItem orderItem);

    @Delete
    void delete(OrderItem orderItem);

    @Query("SELECT * FROM order_items")
    List<OrderItem> getAllOrderItems();

    @Query("SELECT * FROM order_items WHERE id = :id")
    OrderItem getOrderItemById(int id);

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    List<OrderItem> getOrderItemsByOrderId(int orderId);

    @Query("SELECT * FROM order_items WHERE productId = :productId")
    List<OrderItem> getOrderItemsByProductId(int productId);
} 