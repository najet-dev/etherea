import { Component, OnInit } from '@angular/core';
import { CartService } from 'src/app/services/cart.service';
import { Cart } from 'src/app/components/models/cart.model';
import { ProductService } from 'src/app/services/product.service';
import { AuthService } from 'src/app/services/auth.service'; // Ajout du service AuthService

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
})
export class CartComponent implements OnInit {
  cartItems: Cart[] = [];
  cartTotal: number = 0; // Ajouter la propriété pour stocker le total du panier
  userId!: number; // Déclaration de l'ID de l'utilisateur
  isCartEmpty: boolean = true;

  constructor(
    private cartService: CartService,
    private productService: ProductService,
    private authService: AuthService // Injection du service AuthService
  ) {
    // Écouter l'événement de mise à jour du panier
    this.cartService.cartUpdated.subscribe(() => {
      // Recharger les données du panier
      this.loadCartItems();
    });
  }

  ngOnInit(): void {
    // Récupérer l'ID de l'utilisateur actuel, s'il est connecté
    this.authService.getCurrentUser().subscribe((user) => {
      if (user) {
        this.userId = user.id;
        this.loadCartItems();
      } else {
        // Si l'utilisateur n'est pas connecté, utilisez une valeur par défaut pour l'ID de l'utilisateur
        this.userId = 1; // ou toute autre valeur par défaut
        this.loadCartItems();
      }
    });
  }
  loadCartItems() {
    console.log(
      "Chargement des éléments du panier pour l'utilisateur avec l'ID :",
      this.userId
    );
    this.cartService.getCartItems(this.userId).subscribe({
      next: (cartItems) => {
        console.log('Cart items received in component:', cartItems);
        this.cartItems = cartItems;
        this.isCartEmpty = this.cartItems.length === 0;
        for (let i = 0; i < this.cartItems.length; i++) {
          const item = this.cartItems[i];
          this.productService.getProductById(item.productId).subscribe({
            next: (product) => {
              item.product = product;
              this.calculateCartTotal();
            },
            error: (error) => {
              console.log('Erreur lors de la récupération du produit :', error);
            },
          });
        }
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
      .updateCartItem(this.userId, item.productId, item.quantity)
      .subscribe({
        next: (updatedItem) => {
          console.log('Élément du panier mis à jour avec succès');
          // Mettre à jour l'élément du panier dans votre liste cartItems avec les données mises à jour
          const index = this.cartItems.findIndex(
            (cartItem) => cartItem.productId === updatedItem.productId
          );
          if (index !== -1) {
            this.cartItems[index] = updatedItem;
          }
          // Recalculer les sous-totaux et le total du panier
          this.calculateCartTotal();
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
  deleteCartItem(item: Cart): void {
    this.cartService.deleteCartItem(item.id).subscribe(() => {
      // Envoyer une requête pour supprimer l'élément du panier
      this.loadCartItems(); // Recharger les éléments du panier après la suppression
    });
  }
}
