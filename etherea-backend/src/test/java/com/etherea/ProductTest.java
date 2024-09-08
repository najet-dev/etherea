package com.etherea;

import com.etherea.enums.ProductType;
import com.etherea.enums.StockStatus;
import com.etherea.models.Product;
import com.etherea.models.Volume;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

public class ProductTest {

    private Product product;

    @BeforeEach
    public void setUp() {
        // Initialisation avant chaque test
        product = new Product("Product A", "Description A", ProductType.HAIR, StockStatus.OUT_OF_STOCK, "Benefits A", "Usage Tips A", "Ingredients A", "Characteristics A", "Image A");
    }

    @Test
    public void testAddAndRemoveVolume() {
        // Créez des volumes
        Volume volume1 = new Volume("50 ml", new BigDecimal("10.00"), product);
        Volume volume2 = new Volume("100 ml", new BigDecimal("15.00"), product);

        // Ajoutez les volumes au produit
        product.addVolume(volume1);
        product.addVolume(volume2);

        // Vérifiez que les volumes ont été ajoutés
        assertEquals(2, product.getVolumes().size());
        assertTrue(product.getVolumes().contains(volume1));
        assertTrue(product.getVolumes().contains(volume2));

        // Supprimez un volume
        product.removeVolume(volume1);

        // Vérifiez que le volume a été supprimé
        assertEquals(1, product.getVolumes().size());
        assertFalse(product.getVolumes().contains(volume1));
        assertTrue(product.getVolumes().contains(volume2));
    }
}
