package com.example.trunghieu;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.contrib.RecyclerViewActions;

import com.example.trunghieu.database.AppDatabase;
import com.example.trunghieu.models.Product;
import com.example.trunghieu.models.CartItem;
import com.example.trunghieu.models.Order;
import com.example.trunghieu.models.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;

@RunWith(AndroidJUnit4.class)
public class UITest {
    private static final int TEST_USER_ID = 9999;

    @Before
    public void setupUserId() {
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        prefs.edit().putInt("userId", TEST_USER_ID).apply();
    }

    @Test
    public void testLoginActivity() {
        ActivityScenario.launch(LoginActivity.class);
        Espresso.onView(ViewMatchers.withId(R.id.etUsername))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.etPassword))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.btnLogin))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.btnRegister))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testRegisterActivity() {
        ActivityScenario.launch(RegisterActivity.class);
        Espresso.onView(ViewMatchers.withId(R.id.etUsername))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.etPassword))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.etEmail))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.etFullName))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.etAddress))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.etPhone))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.btnRegister))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testMainActivity() {
        Context context = ApplicationProvider.getApplicationContext();
        Product product = new Product("Test Product", "Description", 9.99, "", 10);
        AppDatabase.getInstance(context).productDao().insert(product);
        ActivityScenario.launch(MainActivity.class);
        Espresso.onView(ViewMatchers.withId(R.id.toolbar))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.searchView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testProductItem() {
        Context context = ApplicationProvider.getApplicationContext();
        Product product = new Product("Espresso Test", "Espresso Description", 19.99, "", 5);
        AppDatabase.getInstance(context).productDao().insert(product);
        ActivityScenario.launch(MainActivity.class);
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testCartActivity() {
        Context context = ApplicationProvider.getApplicationContext();
        // Insert test user
        User user = new User("testuser", "password", "test@example.com", "Test User", "123 Test St", "1234567890");
        long userId = AppDatabase.getInstance(context).userDao().insert(user);
        // Insert test product
        Product product = new Product("Cart Test", "Cart Description", 29.99, "", 3);
        long productId = AppDatabase.getInstance(context).productDao().insert(product);
        // Insert cart item with correct userId and productId
        CartItem cartItem = new CartItem((int)userId, (int)productId, 2);
        cartItem.setProduct(product);
        AppDatabase.getInstance(context).cartItemDao().insert(cartItem);
        ActivityScenario.launch(CartActivity.class);
        Espresso.onView(ViewMatchers.withId(R.id.toolbar))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.totalPriceTextView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.checkoutButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testCartItem() {
        Context context = ApplicationProvider.getApplicationContext();
        // Insert test user
        User user = new User("testuser2", "password", "test2@example.com", "Test User2", "456 Test St", "0987654321");
        long userId = AppDatabase.getInstance(context).userDao().insert(user);
        // Insert test product
        Product product = new Product("CartItem Test", "CartItem Description", 39.99, "", 2);
        long productId = AppDatabase.getInstance(context).productDao().insert(product);
        // Insert cart item with correct userId and productId
        CartItem cartItem = new CartItem((int)userId, (int)productId, 1);
        cartItem.setProduct(product);
        AppDatabase.getInstance(context).cartItemDao().insert(cartItem);
        ActivityScenario.launch(CartActivity.class);
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testOrderItem() {
        Context context = ApplicationProvider.getApplicationContext();
        // Insert test user
        User user = new User("testuser3", "password", "test3@example.com", "Test User3", "789 Test St", "1122334455");
        long userId = AppDatabase.getInstance(context).userDao().insert(user);
        // Insert order with correct userId
        Order order = new Order((int)userId, "2023-01-01T10:00:00.000Z", 99.99, "CONFIRMED", "123 Main St", "COD");
        AppDatabase.getInstance(context).orderDao().insert(order);
        ActivityScenario.launch(OrderHistoryActivity.class);
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
} 