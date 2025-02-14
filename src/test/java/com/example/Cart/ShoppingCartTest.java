package com.example.Cart;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ShoppingCartTest {

    @Test
    void shouldAddItemToCart() {
        // Arrange
        ShoppingCart cart = new ShoppingCart();
        Item item = new Item("Apple", 10.0);

        // Act
        cart.addItem(item, 2);

        // Assert
        assertThat(cart.getTotalItems()).isEqualTo(1);
        assertThat(cart.getTotalPrice()).isEqualTo(20.0);
    }

    @Test
    void shouldRemoveItemFromCart() {
        ShoppingCart cart = new ShoppingCart();
        Item item = new Item("Pear", 10.0);
        cart.addItem(item, 2);

        cart.removeItem(item);

        assertThat(cart.getTotalItems()).isEqualTo(0);
        assertThat(cart.getTotalPrice()).isEqualTo(0.0);
        //Har nu gjort Red delen av att ta bort items, removeItem finns inte än
    }

    @Test
    void shouldUpdateItemQuantity() {
        ShoppingCart cart = new ShoppingCart();
        Item item = new Item("Grapes", 10.0);
        cart.addItem(item, 2);

        cart.updateQuantity(item, 5);

        assertThat(cart.getTotalPrice()).isEqualTo(50.0);
        //updateQuantity saknas
    }

    @Test
    void shouldApplyDiscount() {
        ShoppingCart cart = new ShoppingCart();
        Item item = new Item("Laptop", 10000.0);
        cart.addItem(item, 1);

        cart.applyDiscount(20); // 20% rabatt

        assertThat(cart.getTotalPrice()).isEqualTo(8000.0);
        // applyDiscount existerar inte än, ska nu läggas till i shoppingCart klassen
    }
}
