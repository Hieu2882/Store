package com.example.trunghieu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trunghieu.adapters.CartAdapter;
import com.example.trunghieu.api.RetrofitClient;
import com.example.trunghieu.models.CartItem;
import com.example.trunghieu.models.Product;
import com.example.trunghieu.database.AppDatabase;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private TextView totalPriceTextView;
    private Button checkoutButton;
    private List<CartItem> cartItems;
    private int userId; // Get this from SharedPreferences or other storage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Shopping Cart");

        recyclerView = findViewById(R.id.recyclerView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        checkoutButton = findViewById(R.id.checkoutButton);
        cartItems = new ArrayList<>();

        // Lấy userId từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(this, cartItems, new CartAdapter.CartAdapterListener() {
            @Override
            public void onQuantityChanged(CartItem item, int newQuantity) {
                updateCartItem(item, newQuantity);
            }

            @Override
            public void onRemoveItem(CartItem item) {
                removeFromCart(item);
            }
        });
        recyclerView.setAdapter(adapter);

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkout();
            }
        });

        loadCartItems();
    }

    private void loadCartItems() {
        RetrofitClient.getInstance()
                .getApiService()
                .getCartItems(userId)
                .enqueue(new Callback<List<CartItem>>() {
                    @Override
                    public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            cartItems.clear();
                            cartItems.addAll(response.body());
                            adapter.notifyDataSetChanged();
                            updateTotalPrice();
                        } else {
                            Toast.makeText(CartActivity.this, "Failed to load cart items", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CartItem>> call, Throwable t) {
                        Toast.makeText(CartActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateCartItem(CartItem item, int newQuantity) {
        item.setQuantity(newQuantity);
        RetrofitClient.getInstance()
                .getApiService()
                .updateCartItem(item.getId(), item)
                .enqueue(new Callback<CartItem>() {
                    @Override
                    public void onResponse(Call<CartItem> call, Response<CartItem> response) {
                        if (response.isSuccessful()) {
                            updateTotalPrice();
                        } else {
                            Toast.makeText(CartActivity.this, "Failed to update cart", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CartItem> call, Throwable t) {
                        Toast.makeText(CartActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeFromCart(CartItem item) {
        RetrofitClient.getInstance()
                .getApiService()
                .removeFromCart(item.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            cartItems.remove(item);
                            adapter.notifyDataSetChanged();
                            updateTotalPrice();
                        } else {
                            Toast.makeText(CartActivity.this, "Failed to remove item", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(CartActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (product != null) {
                total += item.getQuantity() * product.getPrice();
            }
        }
        totalPriceTextView.setText(String.format("Total: $%.2f", total));
    }

    private void checkout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        // Giả lập địa chỉ và phương thức thanh toán
        String shippingAddress = "123 Main St, City";
        String paymentMethod = "COD";
        double totalAmount = 0;
        List<Object> items = new ArrayList<>();
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (product != null) {
                totalAmount += item.getQuantity() * product.getPrice();
                java.util.HashMap<String, Object> orderItem = new java.util.HashMap<>();
                orderItem.put("productId", product.getId());
                orderItem.put("quantity", item.getQuantity());
                orderItem.put("price", product.getPrice());
                items.add(orderItem);
            }
        }
        java.util.HashMap<String, Object> orderData = new java.util.HashMap<>();
        orderData.put("userId", userId);
        orderData.put("totalAmount", totalAmount);
        orderData.put("shippingAddress", shippingAddress);
        orderData.put("paymentMethod", paymentMethod);
        orderData.put("items", items);

        RetrofitClient.getInstance()
                .getApiService()
                .createOrder(orderData)
                .enqueue(new retrofit2.Callback<com.example.trunghieu.models.Order>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.example.trunghieu.models.Order> call, retrofit2.Response<com.example.trunghieu.models.Order> response) {
                        if (response.isSuccessful()) {
                            cartItems.clear();
                            adapter.notifyDataSetChanged();
                            updateTotalPrice();
                            Toast.makeText(CartActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CartActivity.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.example.trunghieu.models.Order> call, Throwable t) {
                        Toast.makeText(CartActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 