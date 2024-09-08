import { Component, OnInit, inject } from '@angular/core';
import { CartService } from 'src/app/services/cart.service';
import { Cart } from 'src/app/components/models/cart.model';
import { ProductService } from 'src/app/services/product.service';
import { AuthService } from 'src/app/services/auth.service';
import { StorageService } from 'src/app/services/storage.service';
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
  userId!: number;
  isCartEmpty: boolean = true;
  showConfirmDelete: boolean = false;
  itemIdToDelete!: number;
  showModal = false;
  private destroyRef = inject(DestroyRef); // Inject DestroyRef

  constructor(private authService: AuthService, private appFacade: AppFacade) {
    this.appFacade.cartService.cartUpdated.subscribe(() => {
      this.loadCartItems();
    });
  }

  ngOnInit(): void {
    this.authService
      .getCurrentUser()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((user) => {
        if (user && user.id) {
          this.userId = user.id;
          this.loadCartItems();
        }
      });
  }

  loadCartItems() {
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
      },
      error: (error) => {
        console.log('Error retrieving cart items:', error);
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
      if (item.product && item.selectedVolume.price) {
        item.subTotal = item.selectedVolume.price * item.quantity;
        this.cartTotal += item.subTotal;
      }
    }
    this.cartTotal = parseFloat(this.cartTotal.toFixed(2));
  }

  confirmDeleteItem(id: number): void {
    this.itemIdToDelete = id;
    this.showConfirmDelete = true;
  }

  deleteItem(): void {
    this.appFacade.cartService.deleteCartItem(this.itemIdToDelete).subscribe({
      next: () => {
        console.log('Product deleted from cart successfully');
        this.showConfirmDelete = false;
        this.loadCartItems();
      },
      error: (error) => {
        console.error('Failed to delete product from cart:', error);
        this.showConfirmDelete = false;
      },
    });
  }

  cancelDelete(): void {
    this.showConfirmDelete = false;
  }
  hideModal(): void {
    this.showModal = false;
  }
}
