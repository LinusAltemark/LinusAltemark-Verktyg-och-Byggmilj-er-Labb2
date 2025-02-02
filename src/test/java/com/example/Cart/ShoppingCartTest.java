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
}
