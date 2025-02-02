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
}

