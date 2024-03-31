import { Component, OnInit } from '@angular/core';
import { CartService } from 'src/app/services/cart.service';
import { Cart } from 'src/app/components/models/cart.model';
import { ProductService } from 'src/app/services/product.service';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
})
export class CartComponent implements OnInit {
  userId: number = 1;
  cartItems: Cart[] = [];
  cartTotal: number = 0; // Ajouter la propriété pour stocker le total du panier

  constructor(
    private cartService: CartService,
    private productService: ProductService
  ) {
    // Écouter l'événement de mise à jour du panier
    this.cartService.cartUpdated.subscribe(() => {
      // Recharger les données du panier
      this.loadCartItems();
    });
  }
  ngOnInit(): void {
    this.loadCartItems();
  }

  loadCartItems() {
    this.cartService.getCartItems(this.userId).subscribe({
      next: (cartItems) => {
        this.cartItems = cartItems;
        // Récupérer les détails du produit pour chaque élément du panier
        this.cartItems.forEach((item) => {
          this.productService.getProductById(item.productId).subscribe({
            next: (product) => {
              item.product = product; // Ajouter les détails du produit à l'élément du panier
            },
            error: (error) => {
              console.log('Erreur lors de la récupération du produit :', error);
            },
          });
        });
        // Recalculer le total du panier à chaque chargement des éléments du panier
        this.calculateCartTotal();
      },
      error: (error) => {
        console.log(
          'Erreur lors de la récupération des éléments du panier :',
          error
        );
      },
    });
  }

  incrementQuantity(item: Cart): void {
    item.quantity++;
    this.updateCartItem(item);
  }

  decrementQuantity(item: Cart): void {
    if (item.quantity > 1) {
      item.quantity--;
      this.updateCartItem(item);
    }
  }

  updateCartItem(item: Cart): void {
    this.cartService
      .updateToCart(this.userId, item.productId, item.quantity)
      .subscribe({
        next: (updatedItem) => {
          console.log('Élément du panier mis à jour avec succès');
          // Mettre à jour l'élément du panier dans votre liste cartItems avec les données mises à jour
          const index = this.cartItems.findIndex(
            (cartItem) => cartItem.productId === updatedItem.productId
          );
          if (index !== -1) {
            this.cartItems[index] = updatedItem;
            // Recalculer le total du panier après avoir mis à jour l'élément du panier
            this.calculateCartTotal();
          }
        },
        error: (error) => {
          console.error(
            "Erreur lors de la mise à jour de l'élément du panier :",
            error
          );
        },
      });
  }

  calculateCartTotal(): void {
    this.cartTotal = 0; // Réinitialiser le total du panier
    for (const item of this.cartItems) {
      if (item.subTotal !== undefined) {
        this.cartTotal += item.subTotal; // Ajouter le sous-total de chaque article au total du panier
      }
    }
    // Formater le total du panier avec deux chiffres après la virgule
    this.cartTotal = parseFloat(this.cartTotal.toFixed(2));
  }
}
