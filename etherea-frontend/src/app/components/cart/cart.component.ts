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

      if (productId) {
        this.appFacade.getProductById(productId).subscribe({
          next: (product) => {
            item.product = product;

            // Vérification que le produit est bien défini
            if (item.product) {
              // Traitement pour les produits de type HAIR
              if (
                item.product.type === ProductType.HAIR &&
                item.product.volumes?.length
              ) {
                const selectedVolume =
                  item.selectedVolume ||
                  item.product.volumes.find(
                    (volume) => volume.id === item.selectedVolume?.id
                  ) ||
                  item.product.volumes[0];

                item.selectedVolume = selectedVolume;

                if (selectedVolume) {
                  item.subTotal = selectedVolume.price * item.quantity;
                }
              } else if (item.product.type === ProductType.FACE) {
                item.subTotal = (item.product.basePrice || 0) * item.quantity;
              }
            } else {
              console.error("Produit non trouvé pour l'ID:", productId);
            }

            this.calculateCartTotal();
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

  calculateCartTotal(): void {
    this.cartTotal = this.cartItems.reduce((total, item) => {
      if (item.product) {
        if (item.product.type === ProductType.HAIR && item.selectedVolume) {
          item.subTotal = item.selectedVolume.price * item.quantity;
        } else if (
          item.product.type === ProductType.FACE &&
          item.product.basePrice !== undefined
        ) {
          item.subTotal = item.product.basePrice * item.quantity;
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
