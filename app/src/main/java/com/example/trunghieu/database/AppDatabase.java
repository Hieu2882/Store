package com.example.trunghieu.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.trunghieu.models.Product;
import com.example.trunghieu.models.User;
import com.example.trunghieu.models.Order;
import com.example.trunghieu.models.OrderItem;
import com.example.trunghieu.models.CartItem;

@Database(entities = {
        Product.class,
        User.class,
        Order.class,
        OrderItem.class,
        CartItem.class
}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract ProductDao productDao();
    public abstract UserDao userDao();
    public abstract OrderDao orderDao();
    public abstract OrderItemDao orderItemDao();
    public abstract CartItemDao cartItemDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "shopping_app_db"
            ).build();
        }
        return instance;
    }
} 