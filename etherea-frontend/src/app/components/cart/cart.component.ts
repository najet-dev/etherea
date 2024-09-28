import { Component, OnInit, inject } from '@angular/core';
import { CartService } from 'src/app/services/cart.service';
import { Cart } from 'src/app/components/models/cart.model';
import { AuthService } from 'src/app/services/auth.service';
import { DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AppFacade } from 'src/app/services/appFacade.service';
import { ProductType } from '../models/i-product.model';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
})
export class CartComponent implements OnInit {
  cartItems: Cart[] = []; // Initialize as an empty array
  cartTotal: number = 0;
  userId: number = 0;
  isCartEmpty: boolean = true;
  showConfirmDelete: boolean = false;
  itemIdToDelete: number = 0;
  showModal = false;
  private destroyRef = inject(DestroyRef);

  constructor(private authService: AuthService, private appFacade: AppFacade) {
    this.appFacade.cartService.cartUpdated.subscribe(() => {
      this.loadCartItems();
    });
  }

  ngOnInit(): void {
    this.authService
      .getCurrentUser()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (user) => {
          if (user?.id) {
            this.userId = user.id;
            this.loadCartItems();
          }
        },
        error: (error) => {
          console.error('Error fetching current user:', error);
        },
      });
  }

  loadCartItems(): void {
    this.appFacade.getCartItems(this.userId).subscribe({
      next: (cartItems) => {
        this.cartItems = cartItems;
        this.isCartEmpty = this.cartItems.length === 0;

        for (let i = 0; i < this.cartItems.length; i++) {
          const item = this.cartItems[i];
          this.appFacade.getProductById(item.productId).subscribe({
            next: (product) => {
              item.product = product;
              this.calculateCartTotal();
            },
            error: (error) => {
              console.log('Error retrieving product:', error);
            },
          });
        }
        this.calculateCartTotal(); // Calculate total after loading items
      },
      error: (error) => {
        console.error(
          'Erreur lors de la récupération des articles du panier :',
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
    this.appFacade.cartService
      .updateCartItem(this.userId, item.productId, item.quantity)
      .subscribe({
        next: (updatedItem) => {
          console.log('Cart item updated successfully');
          const index = this.cartItems.findIndex(
            (cartItem) => cartItem.productId === updatedItem.productId
          );
          if (index !== -1) {
            this.cartItems[index] = updatedItem;
            this.calculateCartTotal();
          }
        },
        error: (error) => {
          console.error('Error updating cart item:', error);
        },
      });
  }

  calculateCartTotal(): void {
    this.cartTotal = 0;
    for (const item of this.cartItems) {
      if (item.product && item.product.price) {
        item.subTotal = item.product.price * item.quantity;
        this.cartTotal += item.subTotal;
      }
    }
    this.cartTotal = parseFloat(this.cartTotal.toFixed(2));
  }

  confirmDeleteItem(id: number): void {
    this.itemIdToDelete = id;
    this.showConfirmDelete = true; // Affiche la modale de confirmation
  }

  deleteItem(): void {
    if (this.itemIdToDelete !== null) {
      // Vérifiez si l'ID est défini
      this.appFacade.deleteCartItem(this.itemIdToDelete).subscribe({
        next: () => {
          console.log('Produit supprimé du panier avec succès');
          this.loadCartItems(); // Recharge les articles du panier après suppression
        },
        error: (error) => {
          console.error(
            'Échec de la suppression du produit du panier :',
            error
          );
        },
      });
      this.showConfirmDelete = false; // Ferme la fenêtre de confirmation
    } else {
      console.error('Aucun ID d’article spécifié pour la suppression.');
    }
  }

  cancelDelete(): void {
    this.showConfirmDelete = false; // Ferme la fenêtre de confirmation
  }

  hideModal(): void {
    this.showModal = false;
  }
}
