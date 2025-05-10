package com.example.trunghieu.api;

import com.example.trunghieu.models.User;
import com.example.trunghieu.models.Product;
import com.example.trunghieu.models.CartItem;
import com.example.trunghieu.models.Order;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ApiServiceTest {
    private MockWebServer mockWebServer;
    private ApiService apiService;

    @Before
    public void setup() throws IOException {
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

    @Test
    public void testRegister() throws Exception {
        // Prepare test data
        User user = new User("testuser", "password", "test@example.com", "Test User", "123 Test St", "1234567890");
        
        // Mock response
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody("{\"id\":1,\"username\":\"testuser\",\"email\":\"test@example.com\"}"));

        // Execute API call
        Call<User> call = apiService.register(user);
        Response<User> response = call.execute();

        // Verify response
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());

        assertEquals("testuser", response.body().getUsername());
        //assertEquals("sai_username", response.body().getUsername());
        assertEquals("test@example.com", response.body().getEmail());
    }

    @Test
    public void testLogin() throws Exception {
        // Prepare test data
        User user = new User("testuser", "password", "", "", "", "");
        
        // Mock response
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"id\":1,\"username\":\"testuser\",\"email\":\"test@example.com\"}"));

        // Execute API call
        Call<User> call = apiService.login(user);
        Response<User> response = call.execute();

        // Verify response
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals("testuser", response.body().getUsername());
    }

    @Test
    public void testGetProducts() throws Exception {
        // Mock response
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("[{\"id\":1,\"name\":\"Test Product\",\"price\":99.99}]"));

        // Execute API call
        Call<List<Product>> call = apiService.getProducts();
        Response<List<Product>> response = call.execute();

        // Verify response
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals(1, response.body().size());
        assertEquals("Test Product", response.body().get(0).getName());
        //assertEquals("Test Product Sai", response.body().get(0).getName());
    }

    @Test
    public void testSearchProducts() throws Exception {
        // Mock response
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("[{\"id\":1,\"name\":\"Test Product\",\"price\":99.99}]"));

        // Execute API call
        Call<List<Product>> call = apiService.searchProducts("test");
        Response<List<Product>> response = call.execute();

        // Verify response
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals(1, response.body().size());
    }

    @Test
    public void testGetCartItems() throws Exception {
        // Mock response
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("[{\"id\":1,\"userId\":1,\"productId\":1,\"quantity\":2}]"));

        // Execute API call
        Call<List<CartItem>> call = apiService.getCartItems(1);
        Response<List<CartItem>> response = call.execute();

        // Verify response
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals(1, response.body().size());
        assertEquals(2, response.body().get(0).getQuantity());
    }

    @Test
    public void testCreateOrder() throws Exception {
        // Prepare test data
        Map<String, Object> orderData = Map.of(
            "userId", 1,
            "totalAmount", 199.98,
            "shippingAddress", "123 Test St",
            "paymentMethod", "Credit Card",
            "items", List.of(Map.of(
                "productId", 1,
                "quantity", 2,
                "price", 99.99
            ))
        );

        // Mock response
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody("{\"id\":1,\"userId\":1,\"totalAmount\":199.98}"));

        // Execute API call
        Call<Order> call = apiService.createOrder(orderData);
        Response<Order> response = call.execute();

        // Verify response
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals(1, response.body().getId());
        assertEquals(199.98, response.body().getTotalAmount(), 0.01);
    }
} 