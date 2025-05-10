package com.example.trunghieu.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;

@Entity(tableName = "order_items",
        foreignKeys = {
            @ForeignKey(entity = Order.class,
                       parentColumns = "id",
                       childColumns = "orderId",
                       onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = Product.class,
                       parentColumns = "id",
                       childColumns = "productId",
                       onDelete = ForeignKey.CASCADE)
        })
public class OrderItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int orderId;
    private int productId;
    private int quantity;
    private double price;

    public OrderItem(int orderId, int productId, int quantity, double price) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
} 