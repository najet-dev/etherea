import { Component, OnInit } from '@angular/core';
import { CartService } from 'src/app/services/cart.service';
import { Cart } from 'src/app/components/models/cart.model';
import { ProductService } from 'src/app/services/product.service';
import { AuthService } from 'src/app/services/auth.service';
import { StorageService } from 'src/app/services/storage.service';

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
    private authService: AuthService,
    private storageService: StorageService
  ) {
    this.cartService.cartUpdated.subscribe(() => {
      this.loadCartItems();
    });
  }

  ngOnInit(): void {
    this.authService.getCurrentUser().subscribe((user) => {
      if (user && user.id) {
        this.userId = user.id;
        this.loadCartItems();
      }
    });
  }

  loadCartItems() {
    this.cartService.getCartItems(this.userId).subscribe({
      next: (cartItems) => {
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
    this.cartService
      .updateCartItem(this.userId, item.productId, item.quantity) // Utilisez item.productId comme productId
      .subscribe({
        next: (updatedItem) => {
          console.log('Cart item updated successfully');
          const index = this.cartItems.findIndex(
            (cartItem) => cartItem.productId === updatedItem.productId // Utilisez cartItem.productId pour comparer les produits
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

  deleteItemFromCart(id: number): void {
    this.cartService.deleteCartItem(id).subscribe({
      next: () => {
        console.log('Product deleted from cart successfully');
      },
      error: (error) => {
        console.error('Failed to delete product from cart:', error);
      },
    });
  }
}
