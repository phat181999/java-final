package com.example.demo.controller;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.example.demo.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Before
    public void setup(){
        User user = createUser();

        when(userRepository.findByUsername("tanphat")).thenReturn(user);
    }


    public void verify_addToCart() {
        // Existing conditions...
    
        // Submit order with invalid username
        ResponseEntity<UserOrder> invalidUsernameResponse = orderController.submit("invalid username");
        assertNotNull(invalidUsernameResponse);
        assertEquals(404, invalidUsernameResponse.getStatusCodeValue());
        assertNull(invalidUsernameResponse.getBody());
        verify(userRepository, times(1)).findByUsername("invalid username");
    
        // Submit order with empty cart
    
        ResponseEntity<UserOrder> emptyCartResponse = orderController.submit("user_with_empty_cart");
        assertNotNull(emptyCartResponse);
        assertEquals(400, emptyCartResponse.getStatusCodeValue());
        assertNull(emptyCartResponse.getBody());
    }


}
