package com.example.trunghieu.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.OnConflictStrategy;

import com.example.trunghieu.models.Product;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    long insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);

    @Query("SELECT * FROM products")
    List<Product> getAllProducts();

    @Query("SELECT * FROM products WHERE id = :id")
    Product getProductById(int id);

    @Query("SELECT * FROM products WHERE name LIKE '%' || :searchQuery || '%'")
    List<Product> searchProducts(String searchQuery);

    @Query("SELECT * FROM products WHERE stockQuantity > 0")
    List<Product> getAvailableProducts();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Product> products);
} 