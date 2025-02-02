package com.example.Cart;

import java.util.HashMap;
import java.util.Map;

class ShoppingCart {
    private final Map<Item, Integer> items = new HashMap<>();

    void addItem(Item item, int quantity) {
        items.merge(item, quantity, Integer::sum);
    }

    int getTotalItems() {
        return items.size();
    }

    double getTotalPrice() {
        return items.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }

    //Fixade så att removeItem nu existerar
    /*void removeItem(Item item) {
        items.remove(item);
    }*/

    //Refactor för att ge metoden bättre felhantering
    void removeItem(Item item) {
        if (!items.containsKey(item)) {
            throw new IllegalArgumentException("Item not found in cart");
        }
        items.remove(item);
    }

    //Nu finns updateQuantity
    /*void updateQuantity(Item item, int quantity) {
        items.put(item, quantity);
    }*/

    //Ser till att Quantity inte kan vara negativt
    void updateQuantity(Item item, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        items.put(item, quantity);
    }
}

