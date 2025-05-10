package com.example.trunghieu.api;

import com.example.trunghieu.models.Product;
import com.example.trunghieu.models.User;
import com.example.trunghieu.models.Order;
import com.example.trunghieu.models.OrderItem;
import com.example.trunghieu.models.CartItem;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // User endpoints
    @POST("api/users/register")
    Call<User> register(@Body User user);

    @POST("api/users/login")
    Call<User> login(@Body User user);

    @GET("api/users/{id}")
    Call<User> getUser(@Path("id") int id);

    @PUT("api/users/{id}")
    Call<User> updateUser(@Path("id") int id, @Body User user);

    // Product endpoints
    @GET("api/products")
    Call<List<Product>> getProducts();

    @GET("api/products/{id}")
    Call<Product> getProduct(@Path("id") int id);

    @GET("api/products/search")
    Call<List<Product>> searchProducts(@Query("query") String query);

    @POST("products")
    Call<Product> createProduct(@Body Product product);

    @PUT("products/{id}")
    Call<Product> updateProduct(@Path("id") int id, @Body Product product);

    @DELETE("products/{id}")
    Call<Void> deleteProduct(@Path("id") int id);

    // Cart endpoints
    @GET("api/cart/{userId}")
    Call<List<CartItem>> getCartItems(@Path("userId") int userId);

    @POST("api/cart")
    Call<CartItem> addToCart(@Body CartItem cartItem);

    @PUT("api/cart/{id}")
    Call<CartItem> updateCartItem(@Path("id") int id, @Body CartItem cartItem);

    @DELETE("api/cart/{id}")
    Call<Void> removeFromCart(@Path("id") int id);

    @DELETE("api/cart/user/{userId}")
    Call<Void> clearCart(@Path("userId") int userId);

    // Order endpoints
    @GET("api/orders")
    Call<List<Order>> getOrders();

    @GET("api/orders/{id}")
    Call<Order> getOrder(@Path("id") int id);

    @GET("api/orders/user/{userId}")
    Call<List<Order>> getUserOrders(@Path("userId") int userId);

    @POST("api/orders")
    Call<Order> createOrder(@Body Map<String, Object> orderData);

    @PUT("api/orders/{id}")
    Call<Order> updateOrder(@Path("id") int id, @Body Order order);


} 