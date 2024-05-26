import { Component, OnInit } from '@angular/core';
import { switchMap, catchError, tap } from 'rxjs/operators';
import { IProduct } from '../models/i-product';
import { ProductService } from 'src/app/services/product.service';
import { ActivatedRoute } from '@angular/router';
import { CartService } from 'src/app/services/cart.service';
import { Cart } from '../models/cart.model';
import { ProductSummaryComponent } from '../product-summary/product-summary.component';
import { MatDialog } from '@angular/material/dialog';
import { AuthService } from 'src/app/services/auth.service';
import { SigninRequest } from '../models/signinRequest.model';
import { DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { of } from 'rxjs';
import { AppFacade } from 'src/app/services/appFacade.service';

@Component({
  selector: 'app-product-details',
  templateUrl: './productDetails.component.html',
  styleUrls: ['./productDetails.component.css'],
})
export class ProductDetailsComponent implements OnInit {
  product: IProduct | null = null;
  userId: number | null = null;
  cartItem: Cart = {
    id: 0,
    userId: 0,
    productId: 1,
    quantity: 1,
    product: {
      id: 1,
      name: '',
      description: '',
      price: 0,
      type: '',
      stockStatus: '',
      benefits: '',
      usageTips: '',
      ingredients: '',
      characteristics: '',
      image: '',
    },
  };
  limitReached = false;
  stockMessage: string = '';
  private destroyRef = inject(DestroyRef);

  constructor(
    private route: ActivatedRoute,
    private appFacade: AppFacade,
    private dialog: MatDialog,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadProductDetails();
    this.loadCurrentUser();
  }

  loadProductDetails(): void {
    this.route.params
      .pipe(
        switchMap((params) => {
          const id = params['id'];
          return this.appFacade.getProductById(id);
        }),
        catchError((error) => {
          console.error('Error fetching product:', error);
          return of(null);
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe((product) => {
        if (product) {
          this.product = product;
          this.cartItem.productId = product.id;
          this.cartItem.product = { ...product };
          this.updateStockMessage(product.stockStatus);
        }
      });
  }

  loadCurrentUser(): void {
    this.authService
      .getCurrentUser()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (user: SigninRequest | null) => {
          this.userId = user ? user.id : null;
        },
        error: (error) => {
          console.error('Error getting current user ID:', error);
        },
      });
  }

  updateStockMessage(stockStatus: string): void {
    switch (stockStatus) {
      case 'AVAILABLE':
        this.stockMessage = `Le produit est disponible.`;
        break;
      case 'OUT_OF_STOCK':
        this.stockMessage = `Le produit est actuellement en rupture de stock.`;
        break;
      default:
        this.stockMessage = 'Le statut du stock du produit est inconnu.';
    }
  }

  incrementQuantity(): void {
    if (this.cartItem.quantity < 8) {
      this.cartItem.quantity++;
    }
  }

  decrementQuantity(): void {
    if (this.cartItem.quantity > 1) {
      this.cartItem.quantity--;
    }
  }

  addToCart(): void {
    if (this.userId !== null) {
      const subTotal = this.cartItem.quantity * this.cartItem.product.price;
      this.cartItem.subTotal = subTotal;
      this.cartItem.userId = this.userId;

      this.appFacade.cartService
        .addToCart(this.cartItem)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: () => {
            console.log('Product added to cart');
            this.openProductSummaryDialog();
            this.resetCartItem();
          },
          error: (error) => {
            console.error('Error adding product to cart:', error);
          },
        });
    } else {
      console.error('User ID is not available.');
    }
  }

  openProductSummaryDialog(): void {
    const dialogRef = this.dialog.open(ProductSummaryComponent, {
      width: '600px',
      height: '900px',
      data: {
        product: this.product,
        cart: this.cartItem,
        quantity: this.cartItem.quantity,
        subTotal: this.cartItem.subTotal,
      },
    });

    dialogRef.afterClosed().subscribe(() => {
      console.log('The dialog was closed');
    });
  }

  private resetCartItem(): void {
    this.cartItem = {
      id: 0,
      userId: 0,
      productId: 1,
      quantity: 1,
      product: {
        id: 0,
        name: '',
        description: '',
        price: 0,
        type: '',
        stockStatus: '',
        benefits: '',
        usageTips: '',
        ingredients: '',
        characteristics: '',
        image: '',
      },
    };
  }
}
