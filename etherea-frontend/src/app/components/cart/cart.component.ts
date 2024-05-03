import { Component, OnInit } from '@angular/core';
import { CartService } from 'src/app/services/cart.service';
import { Cart } from 'src/app/components/models/cart.model';
import { ProductService } from 'src/app/services/product.service';
import { AuthService } from 'src/app/services/auth.service'; // Ajout du service AuthService
import { StorageService } from 'src/app/services/storage.service';

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
    private authService: AuthService,
    private storageService: StorageService
  ) {
    // Écouter l'événement de mise à jour du panier
    this.cartService.cartUpdated.subscribe(() => {
      // Recharger les données du panier
      this.loadCartItems();
    });
  }

  ngOnInit(): void {
    this.authService.getCurrentUser().subscribe((user) => {
      if (user) {
        this.userId = user.id;

        // Synchroniser le panier avec le serveur
        this.cartService.syncCartWithServer(this.userId).subscribe(
          (response) => {
            // Gérer la réponse réussie du backend ici
            console.log('Réponse réussie du backend:', response);
          },
          (error) => {
            // Gérer l'erreur ici
            console.error(
              'Erreur lors de la synchronisation du panier:',
              error
            );
          }
        );

        this.loadCartItems(); // Charger les éléments du panier après la synchronisation
      } else {
        this.loadCartItems(); // Charger le panier local si non connecté
      }
    });
  }

  loadCartItems() {
    this.cartService.getCartItems(this.userId).subscribe({
      next: (cartItems) => {
        this.cartItems = cartItems;
        this.isCartEmpty = this.cartItems.length === 0;

        // Récupérer les détails du produit pour chaque élément du panier
        for (let i = 0; i < this.cartItems.length; i++) {
          const item = this.cartItems[i];
          this.productService.getProductById(item.productId).subscribe({
            next: (product) => {
              item.product = product; // Ajouter les détails du produit à l'élément du panier
              this.calculateCartTotal(); // Recalculer le total du panier à chaque chargement des éléments du panier
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
    if (this.userId) {
      // Si l'utilisateur est connecté, mettre à jour le panier côté serveur
      this.cartService.updateCartItem(item).subscribe({
        next: () => {
          console.log(
            "Quantité de l'élément de panier mise à jour avec succès"
          );
          this.loadCartItems(); // Recharger les éléments du panier après la mise à jour
        },
        error: (error) => {
          console.error(
            "Erreur lors de la mise à jour de la quantité de l'élément de panier :",
            error
          );
        },
      });
    } else {
      // Si l'utilisateur n'est pas connecté, mettre à jour le panier localement
      const index = this.cartItems.findIndex(
        (cartItem) => cartItem.productId === item.productId
      );
      if (index !== -1) {
        this.cartItems[index].quantity = item.quantity;
        this.storageService.saveLocalCart(this.cartItems); // Sauvegarder le panier localement après la mise à jour
        this.calculateCartTotal(); // Recalculer le total du panier après avoir mis à jour l'élément du panier
      }
    }
  }

  calculateCartTotal(): void {
    this.cartTotal = 0; // Réinitialiser le total du panier
    for (const item of this.cartItems) {
      if (item.product && item.product.price) {
        item.subTotal = item.product.price * item.quantity; // Calculer le sous-total pour chaque article
        this.cartTotal += item.subTotal; // Ajouter le sous-total de chaque article au total du panier
      }
    }
    // Formater le total du panier avec deux chiffres après la virgule
    this.cartTotal = parseFloat(this.cartTotal.toFixed(2));
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
