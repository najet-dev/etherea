import { Component, OnInit } from '@angular/core';
import { CartService } from 'src/app/services/cart.service';
import { Cart } from 'src/app/components/models/cart.model';
import { ProductService } from 'src/app/services/product.service';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
})
export class CartComponent implements OnInit {
  cartItems: Cart[] = [];
  cartTotal: number = 0;
  userId!: number;
  isCartEmpty: boolean = true;

  constructor(
    private cartService: CartService,
    private productService: ProductService,
    private authService: AuthService
  ) {
    // Écoute l'événement de mise à jour du panier
    this.cartService.cartUpdated.subscribe(() => {
      this.loadCartItems();
    });
  }

  ngOnInit(): void {
    // Récupérer l'ID de l'utilisateur actuel
    this.authService.getCurrentUser().subscribe((user) => {
      if (user) {
        this.userId = user.id;

        // Sauvegarder le panier local dans la base de données
        this.cartService.saveLocalCartToBackend(this.userId);

        // Charger les éléments du panier
        this.loadCartItems();
      } else {
        // Si non connecté, charger le panier local
        this.loadCartItems();
      }
    });
  }

  loadCartItems() {
    this.cartService.getCartItems(this.userId).subscribe({
      next: (cartItems) => {
        this.cartItems = cartItems;
        this.isCartEmpty = this.cartItems.length === 0;

        // Charger les produits associés
        for (const item of this.cartItems) {
          this.productService.getProductById(item.productId).subscribe({
            next: (product) => {
              item.product = product;
              this.calculateCartTotal();
            },
            error: (error) => {
              console.error(
                'Erreur lors de la récupération du produit :',
                error
              );
            },
          });
        }
      },
      error: (error) => {
        console.error('Erreur lors de la récupération du panier :', error);
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
    this.cartService.updateCartItem(item.productId, item.quantity).subscribe({
      next: () => {
        const index = this.cartItems.findIndex(
          (cartItem) => cartItem.productId === item.productId
        );

        if (index !== -1) {
          // Mettre à jour la quantité et recalculer le sous-total
          this.cartItems[index].quantity = item.quantity;
          this.cartItems[index].subTotal =
            (item.product?.price ?? 0) * item.quantity; // Recalculer le sous-total
        }

        this.calculateCartTotal(); // Recalculer le total du panier
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
    this.cartTotal = 0;
    for (const item of this.cartItems) {
      const price = item.product?.price ?? 0; // Récupérer le prix du produit
      item.subTotal = price * item.quantity; // Recalculer le sous-total pour chaque produit
      this.cartTotal += item.subTotal; // Ajouter le sous-total au total du panier
    }

    this.cartTotal = parseFloat(this.cartTotal.toFixed(2)); // Fixer à deux décimales
  }

  deleteCartItem(item: Cart): void {
    this.cartService.deleteCartItem(item.id).subscribe({
      next: () => {
        this.loadCartItems(); // Recharger le panier après suppression
      },
      error: (error) => {
        console.error(
          "Erreur lors de la suppression de l'élément du panier :",
          error
        );
      },
    });
  }
}
