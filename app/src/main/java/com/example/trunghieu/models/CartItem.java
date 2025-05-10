package com.example.trunghieu.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

@Entity(tableName = "cart_items",
        foreignKeys = {
            @ForeignKey(entity = User.class,
                       parentColumns = "id",
                       childColumns = "userId",
                       onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = Product.class,
                       parentColumns = "id",
                       childColumns = "productId",
                       onDelete = ForeignKey.CASCADE)
        })
public class CartItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId;
    private int productId;
    private int quantity;
    @Ignore
    private Product product;

    public CartItem(int userId, int productId, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Invalid quantity");
        }
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
} 