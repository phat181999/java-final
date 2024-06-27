package com.example.demo.controller;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static com.example.demo.TestUtils.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ItemControllerTest {

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;
    @Before
    public void setup(){

        when(itemRepository.findById(1L)).thenReturn(Optional.of(createItem(1)));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(createItem(2)));
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());  // Non-existent ID
        when(itemRepository.findAll()).thenReturn(createItems());
        when(itemRepository.findByName("item")).thenReturn(Arrays.asList(createItem(1), createItem(2)));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Save item
        doNothing().when(itemRepository).deleteById(1L);  // Delete item by ID
    
        // Additional conditions
        when(itemRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.existsById(999L)).thenReturn(false);
        when(itemRepository.count()).thenReturn((long) createItems().size());

    }

    @Test
    public void verify_getItems(){
        // Arrange
        List<Item> expectedItems = createItems();
        when(itemRepository.findAll()).thenReturn(expectedItems);

        // Act
        ResponseEntity<List<Item>> response = itemController.getItems();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(expectedItems.size(), items.size());

        // Check each item individually
        for (int i = 0; i < expectedItems.size(); i++) {
            Item expectedItem = expectedItems.get(i);
            Item actualItem = items.get(i);
            assertEquals(expectedItem.getId(), actualItem.getId());
            assertEquals(expectedItem.getName(), actualItem.getName());
            // Add other property checks as needed
        }

        // Verify that findAll was called exactly once
        verify(itemRepository, times(1)).findAll();
    }
}
