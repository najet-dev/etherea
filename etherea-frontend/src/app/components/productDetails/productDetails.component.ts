import { Component, OnInit, inject, DestroyRef } from '@angular/core';
import { switchMap, catchError, of } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { IProduct } from '../models/i-product';
import { Volume } from '../models/volume.model';
import { Cart } from '../models/cart.model';
import { AppFacade } from 'src/app/services/appFacade.service';
import { AuthService } from 'src/app/services/auth.service';
import { SigninRequest } from '../models/signinRequest.model';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ProductSummaryComponent } from '../product-summary/product-summary.component';

@Component({
  selector: 'app-product-details',
  templateUrl: './productDetails.component.html',
  styleUrls: ['./productDetails.component.css'],
})
export class ProductDetailsComponent implements OnInit {
  product: IProduct | null = null;
  userId: number | null = null;
  selectedVolume: Volume | null = null;
  cartItem: Cart = {
    id: 0,
    userId: 0,
    productId: 1,
    quantity: 1,
    subTotal: 0,
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
      volumes: [],
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
          // Replace this with the actual API call to get product details
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

          // Simulate volumes for the product
          this.product.volumes = [
            { volume: '50ml', price: 15 },
            { volume: '100ml', price: 30 },
            { volume: '200ml', price: 60 },
          ];

          this.selectedVolume = this.product.volumes[0]; // Default to the first volume
          this.cartItem.product.price = this.selectedVolume.price; // Set initial price
          this.updateStockMessage(this.product.stockStatus);
          this.updateSubTotal();
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
    if (this.cartItem.quantity < 10) {
      this.cartItem.quantity++;
      this.updateSubTotal();
    } else {
      this.limitReached = true;
    }
  }

  decrementQuantity(): void {
    if (this.cartItem.quantity > 1) {
      this.cartItem.quantity--;
      this.limitReached = false;
      this.updateSubTotal();
    }
  }

  updateSubTotal(): void {
    if (this.selectedVolume) {
      this.cartItem.subTotal =
        this.cartItem.quantity * this.selectedVolume.price;
    }
  }

  addToCart(): void {
    if (this.userId !== null && this.selectedVolume) {
      this.cartItem.userId = this.userId;
      this.cartItem.product.price = this.selectedVolume.price; // Update cart item price

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
      console.error('User ID or selected volume is not available.');
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
      subTotal: 0,
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
        volumes: [],
      },
    };
  }
}
