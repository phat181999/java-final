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
        if (target == null) {
            throw new IllegalArgumentException("Target object cannot be null");
        }
    
        boolean wasPrivate = false;

        try {
            Field declaredField = target.getClass().getDeclaredField(fieldName);
            if(!declaredField.isAccessible()){
                declaredField.setAccessible(true);
                wasPrivate = true;
            }

            declaredField.set(target, toInject);
            if(wasPrivate){
                declaredField.setAccessible(false);
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }


    }

    public static Cart createUser() {
       
    
        Cart cart = new Cart();
        cart.setId(1L);
        List<Item> items = createItems();
        cart.setItems(items);
        cart.setTotal(items.stream().map(Item::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
    
        return cart;
    }

    public static Cart createCart(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
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
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be greater than zero");
        }
    
        Item item = new Item();
        item.setId(id);

        item.setPrice(BigDecimal.valueOf(id * 1.2));

        item.setName("Item " + item.getId());

        item.setDescription("Description ");
        return item;
    }

}
