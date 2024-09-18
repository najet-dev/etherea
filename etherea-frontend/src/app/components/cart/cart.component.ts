import { Component, OnInit, inject } from '@angular/core';
import { CartService } from 'src/app/services/cart.service';
import { Cart } from 'src/app/components/models/cart.model';
import { ProductService } from 'src/app/services/product.service';
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
          if (user && user.id) {
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
          this.cartItems.forEach((item) => {
            const productId = item.productId || item.product?.id;

            if (productId) {
              this.appFacade.getProductById(productId).subscribe({
                next: (product) => {
                  item.product = product;

                  // Assigner le volume renvoyé par l'API à selectedVolume
                  if (item.volume) {
                    item.selectedVolume = item.volume; // Correction ici
                    console.log(
                      'Volume sélectionné pour cet article :',
                      item.selectedVolume
                    );
                  } else {
                    console.error(
                      `Volume sélectionné manquant pour l'article avec l'ID de produit : ${productId}`
                    );
                  }

                  this.calculateCartTotal();
                },
                error: (error) => {
                  console.error(
                    'Erreur lors de la récupération du produit :',
                    error
                  );
                },
              });
            } else {
              console.error("ID de produit manquant pour l'article :", item);
            }
          });
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
      if (item.product && item.selectedVolume) {
        // Assurez-vous que le sous-total est correctement calculé
        item.subTotal = item.selectedVolume.price * item.quantity;
        return total + item.subTotal;
      } else {
        console.error(
          "Volume sélectionné ou produit manquant pour l'article avec l'ID :",
          item.productId
        );
        return total;
      }
    }, 0);
  }

  confirmDeleteItem(id: number): void {
    console.log('Item ID to delete:', id);
    this.itemIdToDelete = id;
    this.showConfirmDelete = true;
  }

  deleteItem(): void {
    console.log('Deleting item with ID:', this.itemIdToDelete); // Ajoutez un log pour vérifier
    if (this.itemIdToDelete) {
      this.appFacade.cartService.deleteCartItem(this.itemIdToDelete).subscribe({
        next: () => {
          console.log('Product deleted from cart successfully');
          this.showConfirmDelete = false;
          this.loadCartItems(); // Recharge les articles du panier après suppression
        },
        error: (error) => {
          console.error('Failed to delete product from cart:', error);
          this.showConfirmDelete = false;
        },
      });
    } else {
      console.error('No item ID specified for deletion.');
    }
  }

  cancelDelete(): void {
    this.showConfirmDelete = false;
  }

  hideModal(): void {
    this.showModal = false;
  }
}
