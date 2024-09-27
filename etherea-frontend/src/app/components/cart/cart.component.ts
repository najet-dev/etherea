import { Component, OnInit, inject } from '@angular/core';
import { CartService } from 'src/app/services/cart.service';
import { Cart } from 'src/app/components/models/cart.model';
import { AuthService } from 'src/app/services/auth.service';
import { DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AppFacade } from 'src/app/services/appFacade.service';
import { ProductType } from '../models/i-product';
import { take } from 'rxjs';

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
  itemIdToDelete: number | null = null; // Ensure this can be null initially
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
          } else {
            console.error('User not found');
          }
        },
        error: (error) => {
          console.error('Error fetching current user:', error);
        },
      });
  }
  loadCartItems(): void {
    this.authService
      .getCurrentUser()
      .pipe(take(1))
      .subscribe({
        next: (user) => {
          if (user && user.id) {
            this.userId = user.id;
            console.log('Articles du panier:', this.cartItems);

            this.appFacade.getCartItems(this.userId).subscribe({
              next: (cartItems) => {
                this.cartItems = cartItems;
                this.isCartEmpty = this.cartItems.length === 0;

                if (this.cartItems.length > 0) {
                  this.loadProductDetails();
                }
              },
              error: (error) => {
                console.error('Error fetching cart items:', error);
              },
            });
          } else {
            console.error('User not found or ID is undefined');
          }
        },
        error: (error) => {
          console.error('Error fetching current user:', error);
        },
      });
  }

  loadProductDetails(): void {
    this.cartItems.forEach((item) => {
      const productId = item.productId || item.product?.id;

      console.log("Traitement de l'ID du produit:", productId);

      if (productId) {
        this.appFacade.getProductById(productId).subscribe({
          next: (product) => {
            item.product = product;

            // Ici, vous devez assigner le volume sélectionné
            if (product.volumes && product.volumes.length > 0) {
              item.selectedVolume = product.volumes[0]; // Par exemple, sélectionnez le premier volume
            }

            console.log('Produit chargé:', item.product);
            console.log('Volumes disponibles:', item.product.volumes);
            this.calculateCartTotal(); // Toujours recalculer le total
          },
          error: (error) => {
            console.error(
              'Erreur lors de la récupération des détails du produit :',
              error
            );
          },
        });
      } else {
        console.error(
          "Aucun ID de produit trouvé pour l'article du panier:",
          item
        );
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
    if (item && item.userId && item.productId && item.quantity !== undefined) {
      // Log des valeurs
      console.log('userId:', item.userId);
      console.log('productId:', item.productId);
      console.log('quantity:', item.quantity);
      console.log('volumeId:', item.selectedVolume?.id);

      // Si le produit est de type HAIR, inclure le volumeId
      const volumeId =
        item.product.type === 'HAIR' ? item.selectedVolume?.id : undefined;

      this.appFacade
        .updateCartItem(item.userId, item.productId, item.quantity, volumeId)
        .subscribe({
          next: (response) => {
            console.log('Cart item updated successfully', response);
            this.calculateCartTotal();
          },
          error: (error) => {
            console.error('Error updating cart item:', error);
          },
        });
    }
  }
  calculateCartTotal(): void {
    this.cartTotal = this.cartItems.reduce((total, item) => {
      if (item.product) {
        // Vérifiez si le sous-total est bien mis à jour pour chaque item
        if (item.product.type === ProductType.HAIR && item.selectedVolume) {
          item.subTotal = item.selectedVolume.price * item.quantity;
        } else if (item.product.type === ProductType.FACE) {
          item.subTotal = (item.product.basePrice || 0) * item.quantity;
        }

        return total + (item.subTotal || 0);
      }
      return total;
    }, 0);
  }

  confirmDeleteItem(id: number): void {
    this.itemIdToDelete = id;
    this.showConfirmDelete = true; // Show confirmation modal
  }

  deleteItem(): void {
    if (this.itemIdToDelete !== null) {
      // Ensure ID is defined
      this.appFacade.deleteCartItem(this.itemIdToDelete).subscribe({
        next: () => {
          console.log('Item deleted from cart successfully');
          this.loadCartItems(); // Reload cart items after deletion
        },
        error: (error) => {
          console.error('Failed to delete item from cart:', error);
        },
      });
      this.showConfirmDelete = false; // Close confirmation modal
    } else {
      console.error('No item ID specified for deletion.');
    }
  }

  cancelDelete(): void {
    this.showConfirmDelete = false; // Close confirmation modal
  }

  hideModal(): void {
    this.showModal = false; // Hide modal
  }
}
