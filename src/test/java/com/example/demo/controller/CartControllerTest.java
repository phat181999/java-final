package com.example.demo.controller;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;


import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static com.example.demo.TestUtils.*;

@RunWith(MockitoJUnitRunner.class)
public class CartControllerTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @Before
    public void setup(){

        when(itemRepository.findById(1L)).thenReturn(Optional.of(createItem(1)));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(createItem(2)));
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());  // Non-existent ID
        when(itemRepository.findAll()).thenReturn(createItems());
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Save item
        doNothing().when(itemRepository).deleteById(1L);  // Delete item by ID
        when(itemRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.existsById(999L)).thenReturn(false);
        when(itemRepository.count()).thenReturn((long) createItems().size());

    }



    @Test
    public void verify_addToCart(){
            // Arrange
        ModifyCartRequest request = new ModifyCartRequest();
        request.setQuantity(3);
        request.setItemId(1);
        request.setUsername("fymo");
         Item item = createItem(request.getItemId());

        when(itemRepository.findById(request.getItemId())).thenReturn(Optional.of(item));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Save cart

        // Act
        ResponseEntity<Cart> response = cartController.addTocart(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart actualCart = response.getBody();
        assertNotNull(actualCart);

        BigDecimal itemPrice = item.getPrice();

        assertEquals("fymo", actualCart.getUser().getUsername());
        assertEquals(item, actualCart.getItems().get(0));

        verify(cartRepository, times(1)).save(actualCart);

        // Additional conditions
        // Non-existent user
        ModifyCartRequest nonExistentUserRequest = new ModifyCartRequest();
        nonExistentUserRequest.setQuantity(3);
        nonExistentUserRequest.setItemId(1);
        nonExistentUserRequest.setUsername("nonexistent");

        ResponseEntity<Cart> nonExistentUserResponse = cartController.addTocart(nonExistentUserRequest);
        assertEquals(404, nonExistentUserResponse.getStatusCodeValue());

        // Non-existent item
        ModifyCartRequest nonExistentItemRequest = new ModifyCartRequest();
        nonExistentItemRequest.setQuantity(3);
        nonExistentItemRequest.setItemId(999);
        nonExistentItemRequest.setUsername("fymo");

        ResponseEntity<Cart> nonExistentItemResponse = cartController.addTocart(nonExistentItemRequest);
        assertEquals(404, nonExistentItemResponse.getStatusCodeValue());

        // Zero quantity
        ModifyCartRequest zeroQuantityRequest = new ModifyCartRequest();
        zeroQuantityRequest.setQuantity(0);
        zeroQuantityRequest.setItemId(1);
        zeroQuantityRequest.setUsername("fymo");

        ResponseEntity<Cart> zeroQuantityResponse = cartController.addTocart(zeroQuantityRequest);
        assertEquals(400, zeroQuantityResponse.getStatusCodeValue());

        // Negative quantity
        ModifyCartRequest negativeQuantityRequest = new ModifyCartRequest();
        negativeQuantityRequest.setQuantity(-1);
        negativeQuantityRequest.setItemId(1);
        negativeQuantityRequest.setUsername("fymo");

        ResponseEntity<Cart> negativeQuantityResponse = cartController.addTocart(negativeQuantityRequest);
        assertEquals(400, negativeQuantityResponse.getStatusCodeValue());

    }






}
