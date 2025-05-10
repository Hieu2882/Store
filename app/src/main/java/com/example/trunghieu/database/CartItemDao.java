package com.example.trunghieu.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.trunghieu.models.CartItem;

import java.util.List;

@Dao
public interface CartItemDao {
    @Insert
    long insert(CartItem cartItem);

    @Update
    void update(CartItem cartItem);

    @Delete
    void delete(CartItem cartItem);

    @Query("SELECT * FROM cart_items")
    List<CartItem> getAllCartItems();

    @Query("SELECT * FROM cart_items WHERE id = :id")
    CartItem getCartItemById(int id);

    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    List<CartItem> getCartItemsByUserId(int userId);

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId")
    CartItem getCartItemByUserIdAndProductId(int userId, int productId);

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    void clearUserCart(int userId);
} 