package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class TestUtils {

    public static void injectObjects(Object target, String fieldName, Object toInject) {

        boolean wasPrivate = false;
        if (target == null || fieldName == null || toInject == null) {
            throw new IllegalArgumentException("Null argument passed to injectObjects");
        }
    
        try {
            Field declaredField = target.getClass().getDeclaredField(fieldName);

            if(!declaredField.isAccessible()){
                declaredField.setAccessible(true);
                wasPrivate = true;
            }
if (!declaredField.getType().isAssignableFrom(toInject.getClass())) {
            throw new IllegalArgumentException("Cannot inject object of type " + toInject.getClass().getName()
                    + " into field " + fieldName + " of type " + declaredField.getType().getName());
        }
            declaredField.set(target, toInject);
            if(wasPrivate){
                declaredField.setAccessible(false);
            }
            if (!declaredField.isAccessible()) {
                declaredField.setAccessible(false);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }


    }

    public static User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("tanphat");
        user.setPassword("tanphat");
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty() || user.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        user.setCart(createCart(user));

        return user;
    }

    public static Cart createCart(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
    
        Cart cart = new Cart();
        cart.setId(1L);
        List<Item> items = createItems();
        cart.setItems(createItems());
        cart.setTotal(items.stream().map(item -> item.getPrice()).reduce(BigDecimal::add).get());
        cart.setUser(user);

        return cart;
    }

    public static List<Item> createItems() {

        List<Item> items = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            items.add(createItem(i));
        }

        return items;
    }

    public static Item createItem(long id){
        if (id < 0) {
            throw new IllegalArgumentException("cannot be null.");
        }
        Item item = new Item();
        item.setId(id);

        item.setPrice(BigDecimal.valueOf(id * 1.2));

        item.setName("Item " + item.getId());

        item.setDescription("Description ");
        return item;
    }

   

}
