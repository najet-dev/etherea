package com.etherea;

import com.etherea.models.Cart;
import com.etherea.models.CartItem;
import com.etherea.models.Product;
import com.etherea.models.Volume;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CartItemTest {

    @Test
    public void testCalculateSubtotal() {
        // Initialize the Product
        Product product = new Product();

        // Initialize the Volume with a volume size, price, and associate it with the Product
        Volume volume = new Volume("100ml", new BigDecimal("10.0"), product); // Correct order and types

        // Initialize the Cart
        Cart cart = new Cart();

        // Initialize the CartItem with the Volume and Product
        CartItem cartItem = new CartItem(1L, 10, product, volume, cart);

        // Calculate the expected subtotal
        BigDecimal expectedSubtotal = new BigDecimal("10.0").multiply(BigDecimal.valueOf(10)); // Price * Quantity

        // Assert that the subtotal is correctly calculated
        assertEquals(expectedSubtotal, cartItem.calculateSubtotal());

        // Set the subtotal in cartItem
        cartItem.setSubTotal(cartItem.calculateSubtotal());

        // Assert that the subtotal is correctly set
        assertEquals(expectedSubtotal, cartItem.getSubTotal());
    }

    @Test
    public void testCalculateTotalPrice() {
        // Initialize products, volumes, cart, and cart items
        Product product1 = new Product();
        Volume volume1 = new Volume("100ml", new BigDecimal("10.0"), product1);
        Cart cart = new Cart();
        CartItem cartItem1 = new CartItem(1L, 10, product1, volume1, cart);

        Product product2 = new Product();
        Volume volume2 = new Volume("200ml", new BigDecimal("15.0"), product2);
        CartItem cartItem2 = new CartItem(2L, 5, product2, volume2, cart);

        // Calculate total price
        BigDecimal totalPrice = CartItem.calculateTotalPrice(List.of(cartItem1, cartItem2));

        // Calculate expected total
        BigDecimal expectedTotal = new BigDecimal("10.0").multiply(BigDecimal.valueOf(10))
                .add(new BigDecimal("15.0").multiply(BigDecimal.valueOf(5)));

        // Assert the total price is correctly calculated
        assertEquals(expectedTotal, totalPrice);
    }
}
