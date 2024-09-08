import { Component, OnInit } from '@angular/core';
import { switchMap, catchError, tap } from 'rxjs/operators';
import { IProduct } from '../models/i-product.model';
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
import { IProductVolume } from '../models/IProductVolume.model';

@Component({
  selector: 'app-product-details',
  templateUrl: './productDetails.component.html',
  styleUrls: ['./productDetails.component.css'],
})
export class ProductDetailsComponent implements OnInit {
  product: IProduct | null = null;
  selectedVolume: IProductVolume | null = null;
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
      type: '',
      stockStatus: '',
      benefits: '',
      usageTips: '',
      ingredients: '',
      characteristics: '',
      image: '',
      volumes: [],
    },
    selectedVolume: {
      volume: 0,
      price: 0,
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
          this.cartItem.product = { ...product }; // Remove price assignment
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
  onVolumeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const selectedValue = target?.value;

    if (selectedValue && this.product?.volumes) {
      const volume = this.product.volumes.find(
        (vol) => vol.volume.toString() === selectedValue
      );

      if (volume) {
        this.selectedVolume = volume;
        console.log('Volume selected:', this.selectedVolume);
      } else {
        console.error('Selected volume not found in product volumes.');
      }
    } else {
      console.error(
        'Invalid volume selection or product volumes not available.'
      );
    }
  }

  selectVolume(volume: IProductVolume): void {
    this.selectedVolume = volume;
    // Adjust the cart item here if needed
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
    if (!this.userId) {
      console.error('User ID is not available.');
      alert('Vous devez être connecté pour ajouter des articles au panier.');
      return;
    }

    if (!this.selectedVolume) {
      console.error('No volume selected.');
      alert("Veuillez sélectionner un volume avant d'ajouter au panier.");
      return;
    }

    // Proceed with adding to cart
    const subTotal = this.cartItem.quantity * this.selectedVolume.price;
    this.cartItem.subTotal = subTotal;
    this.cartItem.userId = this.userId;
    this.cartItem.selectedVolume = { ...this.selectedVolume };

    this.appFacade
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
        type: '',
        stockStatus: '',
        benefits: '',
        usageTips: '',
        ingredients: '',
        characteristics: '',
        image: '',
        volumes: [],
      },
      selectedVolume: {
        volume: 0,
        price: 0,
      },
    };
    this.selectedVolume = null;
  }
}
