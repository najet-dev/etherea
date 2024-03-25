import { Component, OnInit, OnDestroy } from '@angular/core';
import { CartService } from 'src/app/services/cart.service';
import { ProductService } from 'src/app/services/product.service';
import { Cart } from 'src/app/components/models/cart.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { UserService } from 'src/app/services/user.service'; // Importez le service UserService

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
})
export class CartComponent implements OnInit, OnDestroy {
  userId: number | null = null;
  cartItems: Cart[] = [];
  private unsubscribe$ = new Subject<void>();

  constructor(
    private cartService: CartService,
    private productService: ProductService,
    private userService: UserService // Injectez le service UserService
  ) {}

  ngOnInit(): void {
    this.loadUserIdAndUserCart();
  }

  loadUserIdAndUserCart() {
    this.userService
      .getCurrentUserId()
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe({
        next: (userId) => {
          this.userId = userId;
          if (this.userId !== null) {
            this.loadUserCart();
          }
        },
        error: (error) => {
          console.log('Error fetching current user ID:', error);
        },
      });
  }

  loadUserCart() {
    if (this.userId === null) {
      console.log('User ID is not available.');
      return;
    }

    this.cartService
      .getUserCart(this.userId)
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe({
        next: (response) => {
          this.cartItems = response;
          this.loadProductsDetails();
        },
        error: (error) => {
          console.log('Error fetching user cart:', error);
        },
      });
  }

  loadProductsDetails() {
    this.cartItems.forEach((item) => {
      this.productService
        .getProductById(item.productId)
        .pipe(takeUntil(this.unsubscribe$))
        .subscribe({
          next: (product) => {
            item.product = product;
          },
          error: (error) => {
            console.log('Error fetching product details:', error);
          },
        });
    });
  }

  ngOnDestroy() {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }
}
