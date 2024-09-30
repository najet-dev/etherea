package com.etherea.models;

import com.etherea.enums.ProductType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnore // Ignorer la sérialisation de cette propriété pour éviter la récursion infinie
    private User user;
    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "volumeId")
    private Volume volume;  // Ajout de la référence au volume
    @ManyToOne
    @JoinColumn(name = "cartId")
    @JsonIgnore
    private Cart cart;
    private BigDecimal subTotal; // Modification pour BigDecimal
    private BigDecimal total;    // Modification pour BigDecimal
    public CartItem() {
    }
    public CartItem(Long id, int quantity, Product product, Volume volume, Cart cart) {
        this.id = id;
        this.quantity = quantity;
        this.product = product;
        this.volume = volume;
        this.cart = cart;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Volume getVolume() {
        return volume;
    }

    public void setVolume(Volume volume) {
        this.volume = volume;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    // Méthode pour calculer le sous-total (prix * quantité) d'un produit pour un volume spécifique
    // Méthode pour calculer le sous-total
    public BigDecimal calculateSubtotal() {
        if (product.getType() == ProductType.FACE) {
            // Utiliser basePrice pour les produits de type FACE
            return product.getBasePrice().multiply(BigDecimal.valueOf(quantity));
        } else if (volume != null && volume.getPrice() != null) {
            // Utiliser le prix du volume pour les produits de type HAIR
            return volume.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO; // Retourne 0 si aucune condition n'est remplie
    }
    // Méthode pour calculer le prix total de tous les produits dans le panier
    public static BigDecimal calculateTotalPrice(List<CartItem> items) {
        return items.stream()
                .map(CartItem::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
