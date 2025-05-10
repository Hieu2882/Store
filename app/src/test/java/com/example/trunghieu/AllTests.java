package com.example.trunghieu;

import com.example.trunghieu.api.ApiService;
import com.example.trunghieu.api.RetrofitClient;
import com.example.trunghieu.models.User;
import com.example.trunghieu.models.Product;
import com.example.trunghieu.models.CartItem;
import com.example.trunghieu.models.Order;
import com.example.trunghieu.database.AppDatabase;
import com.example.trunghieu.database.ProductDao;
import com.example.trunghieu.database.UserDao;
import com.example.trunghieu.database.OrderDao;
import com.example.trunghieu.database.CartItemDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AllTests {
    // API Testing
    private MockWebServer mockWebServer;
    private ApiService apiService;

    // Database Testing
    @Mock
    private AppDatabase mockDatabase;
    @Mock
    private ProductDao mockProductDao;
    @Mock
    private UserDao mockUserDao;
    @Mock
    private OrderDao mockOrderDao;
    @Mock
    private CartItemDao mockCartItemDao;

    @Before
    public void setup() throws IOException {
        // Setup MockWebServer for API testing
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    // API Tests
    @Test
    @Category(ApiTests.class)
    public void testApiRegister() throws Exception {
        User user = new User("testuser", "password", "test@example.com", "Test User", "123 Test St", "1234567890");
        
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody("{\"id\":1,\"username\":\"testuser\",\"email\":\"test@example.com\"}"));

        Call<User> call = apiService.register(user);
        Response<User> response = call.execute();

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals("testuser", response.body().getUsername());
        assertEquals("test@example.com", response.body().getEmail());
    }

    @Test
    @Category(ApiTests.class)
    public void testApiRegisterInvalidData() throws Exception {
        User user = new User("", "", "", "", "", "");
        
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\":\"Invalid input data\"}"));

        Call<User> call = apiService.register(user);
        Response<User> response = call.execute();

        assertFalse(response.isSuccessful());
        assertEquals(400, response.code());
    }

    @Test
    @Category(ApiTests.class)
    public void testApiLogin() throws Exception {
        User user = new User("testuser", "password", "", "", "", "");
        
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"id\":1,\"username\":\"testuser\",\"email\":\"test@example.com\"}"));

        Call<User> call = apiService.login(user);
        Response<User> response = call.execute();

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals("testuser", response.body().getUsername());
    }

    @Test
    @Category(ApiTests.class)
    public void testApiLoginInvalidCredentials() throws Exception {
        User user = new User("testuser", "wrongpassword", "", "", "", "");
        
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"error\":\"Invalid credentials\"}"));

        Call<User> call = apiService.login(user);
        Response<User> response = call.execute();

        assertFalse(response.isSuccessful());
        assertEquals(401, response.code());
    }

    @Test
    @Category(ApiTests.class)
    public void testApiGetProducts() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("[{\"id\":1,\"name\":\"Test Product\",\"price\":99.99}]"));

        Call<List<Product>> call = apiService.getProducts();
        Response<List<Product>> response = call.execute();

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals(1, response.body().size());
        assertEquals("Test Product", response.body().get(0).getName());
    }

    @Test
    @Category(ApiTests.class)
    public void testApiGetProductsEmpty() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("[]"));

        Call<List<Product>> call = apiService.getProducts();
        Response<List<Product>> response = call.execute();

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertTrue(response.body().isEmpty());
    }

    // Database Tests
    @Test
    @Category(DatabaseTests.class)
    public void testDatabaseProductOperations() {
        Product product = new Product("Test Product", "Description", 99.99, "image.jpg", 10);
        
        // Test insert
        mockProductDao.insert(product);
        verify(mockProductDao).insert(product);

        // Test get all products
        List<Product> products = mockProductDao.getAllProducts();
        assertNotNull(products);
    }

    @Test
    @Category(DatabaseTests.class)
    public void testDatabaseUserOperations() {
        User user = new User("testuser", "password", "test@example.com", "Test User", "123 Test St", "1234567890");
        
        // Test insert
        mockUserDao.insert(user);
        verify(mockUserDao).insert(user);

        // Setup mock return value
        when(mockUserDao.getUserById(1)).thenReturn(user);
        // Test get user by id
        User retrievedUser = mockUserDao.getUserById(1);
        assertNotNull(retrievedUser);
    }

    @Test
    @Category(DatabaseTests.class)
    public void testDatabaseOrderOperations() {
        Order order = new Order(1, "2024-03-20", 199.98, "PENDING", "123 Test St", "Credit Card");
        
        // Test insert
        mockOrderDao.insert(order);
        verify(mockOrderDao).insert(order);

        // Setup mock return value
        when(mockOrderDao.getOrdersByUserId(1)).thenReturn(java.util.Collections.singletonList(order));
        // Test get user orders
        List<Order> orders = mockOrderDao.getOrdersByUserId(1);
        assertNotNull(orders);
    }

    @Test
    @Category(DatabaseTests.class)
    public void testDatabaseCartOperations() {
        CartItem cartItem = new CartItem(1, 1, 2);
        
        // Test insert
        mockCartItemDao.insert(cartItem);
        verify(mockCartItemDao).insert(cartItem);

        // Setup mock return value
        when(mockCartItemDao.getCartItemsByUserId(1)).thenReturn(java.util.Collections.singletonList(cartItem));
        // Test get user cart
        List<CartItem> cartItems = mockCartItemDao.getCartItemsByUserId(1);
        assertNotNull(cartItems);
    }

    // Model Tests
    @Test
    @Category(ModelTests.class)
    public void testUserModel() {
        User user = new User("testuser", "password", "test@example.com", "Test User", "123 Test St", "1234567890");
        
        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test User", user.getFullName());
        assertEquals("123 Test St", user.getAddress());
        assertEquals("1234567890", user.getPhone());
    }

    @Test
    @Category(ModelTests.class)
    public void testUserModelValidation() {
        // Test with invalid email
        try {
            new User("testuser", "password", "invalid-email", "Test User", "123 Test St", "1234567890");
            fail("Should throw IllegalArgumentException for invalid email");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("email"));
        }

        // Test with invalid phone
        try {
            new User("testuser", "password", "test@example.com", "Test User", "123 Test St", "invalid-phone");
            fail("Should throw IllegalArgumentException for invalid phone");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("phone"));
        }
    }

    @Test
    @Category(ModelTests.class)
    public void testProductModel() {
        Product product = new Product("Test Product", "Description", 99.99, "image.jpg", 10);
        
        assertEquals("Test Product", product.getName());
        assertEquals("Description", product.getDescription());
        assertEquals(99.99, product.getPrice(), 0.01);
        assertEquals("image.jpg", product.getImageUrl());
        assertEquals(10, product.getStockQuantity());
    }

    @Test
    @Category(ModelTests.class)
    public void testProductModelValidation() {
        // Test with negative price
        try {
            new Product("Test Product", "Description", -99.99, "image.jpg", 10);
            fail("Should throw IllegalArgumentException for negative price");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("price"));
        }

        // Test with negative stock
        try {
            new Product("Test Product", "Description", 99.99, "image.jpg", -10);
            fail("Should throw IllegalArgumentException for negative stock");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("stock"));
        }
    }

    @Test
    @Category(ModelTests.class)
    public void testOrderModel() {
        Order order = new Order(1, "2024-03-20", 199.98, "PENDING", "123 Test St", "Credit Card");
        
        assertEquals(1, order.getUserId());
        assertEquals(199.98, order.getTotalAmount(), 0.01);
        assertEquals("123 Test St", order.getShippingAddress());
        assertEquals("Credit Card", order.getPaymentMethod());
    }

    @Test
    @Category(ModelTests.class)
    public void testOrderModelValidation() {
        // Test with negative amount
        try {
            new Order(1, "2024-03-20", -199.98, "PENDING", "123 Test St", "Credit Card");
            fail("Should throw IllegalArgumentException for negative amount");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("amount"));
        }

        // Test with empty address
        try {
            new Order(1, "2024-03-20", 199.98, "PENDING", "", "Credit Card");
            fail("Should throw IllegalArgumentException for empty address");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("address"));
        }
    }

    @Test
    @Category(ModelTests.class)
    public void testCartItemModel() {
        CartItem cartItem = new CartItem(1, 1, 2);
        
        assertEquals(1, cartItem.getUserId());
        assertEquals(1, cartItem.getProductId());
        assertEquals(2, cartItem.getQuantity());
    }

    @Test
    @Category(ModelTests.class)
    public void testCartItemModelValidation() {
        // Test with negative quantity
        try {
            new CartItem(1, 1, -2);
            fail("Should throw IllegalArgumentException for negative quantity");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("quantity"));
        }
    }
}

// Test Categories
interface ApiTests {}
interface DatabaseTests {}
interface ModelTests {} 