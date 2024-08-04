package com.etherea;

import com.etherea.models.Cart;
import com.etherea.models.CartItem;
import com.etherea.models.Product;
import com.etherea.models.Volume;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CartItemTest {

    @Test
    public void testCalculateSubtotal() {
        // Initialize the Product
        Product product = new Product();

        // Initialize the Volume with a price and associate it with the Product
        Volume volume = new Volume(product, "100ml", 10.0); // Assuming 10.0 is the price per unit

        // Initialize the CartItem with the Volume
        CartItem cartItem = new CartItem(1L, 5, 0.0, 0.0, product, volume, new Cart());

        // Set the quantity for the CartItem
        cartItem.setQuantity(10);

        // Calculate the expected subtotal
        double expectedSubtotal = 10.0 * 10; // Price * Quantity

        // Assert that the subtotal is correctly calculated
        assertEquals(expectedSubtotal, cartItem.getSubTotal(), 0.001);
    }
}

