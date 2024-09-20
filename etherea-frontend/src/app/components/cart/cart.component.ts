import { Component, OnInit, inject } from '@angular/core';
import { CartService } from 'src/app/services/cart.service';
import { Cart } from 'src/app/components/models/cart.model';
import { AuthService } from 'src/app/services/auth.service';
import { DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AppFacade } from 'src/app/services/appFacade.service';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
})
export class CartComponent implements OnInit {
  cartItems: Cart[] = [];
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

        if (this.cartItems.length > 0) {
          this.loadProductDetails();
        }
      },
      error: (error) => {
        console.error(
          'Erreur lors de la récupération des articles du panier :',
          error
        );
      },
    });
  }

  private loadProductDetails(): void {
    this.cartItems.forEach((item) => {
      const productId = item.productId || item.product?.id;

      if (productId) {
        this.appFacade.getProductById(productId).subscribe({
          next: (product) => {
            item.product = product;

            // Vérification du type de produit et traitement en conséquence
            if (item.product.type === 'HAIR' && item.selectedVolume) {
              // Produit de type HAIR, utiliser le volume sélectionné
              item.subTotal = item.selectedVolume.price * item.quantity;
            } else if (
              item.product?.type === 'FACE' &&
              item.product.basePrice !== undefined
            ) {
              // Produit de type FACE, utiliser le prix de base
              item.subTotal = item.product.basePrice * item.quantity;
            } else {
              console.error(
                `Type de produit inconnu pour le produit avec ID : ${productId}`
              );
            }

            // Calculer le total du panier après avoir chargé les détails du produit
            this.calculateCartTotal();
          },
          error: (error) => {
            console.error('Erreur lors de la récupération du produit:', error);
          },
        });
      } else {
        console.error("ID de produit manquant pour l'article:", item);
      }
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
    if (item && item.userId && item.productId && item.quantity) {
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
    } else {
      console.error('Invalid item data:', item);
    }
  }

  private calculateCartTotal(): void {
    this.cartTotal = this.cartItems.reduce((total, item) => {
      if (item.product) {
        if (
          item.product.type === 'HAIR' &&
          item.selectedVolume?.price !== undefined
        ) {
          // Produit de type HAIR avec volume sélectionné
          item.subTotal = item.selectedVolume.price * item.quantity;
        } else if (
          item.product.type === 'FACE' &&
          item.product.basePrice !== undefined
        ) {
          // Produit de type FACE avec prix de base
          item.subTotal = item.product.basePrice * item.quantity;
        } else {
          console.error(
            "Volume sélectionné ou prix manquant pour l'article avec l'ID :",
            item.productId
          );
        }

        // Assurez-vous que subTotal n'est pas undefined avant de l'ajouter au total
        return total + (item.subTotal || 0); // Ajoute 0 si subTotal est undefined
      } else {
        return total;
      }
    }, 0);
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
