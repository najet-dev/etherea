import { Component, OnInit, DestroyRef, inject } from '@angular/core';
import { switchMap, catchError, of } from 'rxjs';
import {
  IProduct,
  ProductType as ProductEnum,
  ProductType,
} from '../models/i-product';
import { MatDialog } from '@angular/material/dialog';
import { AuthService } from 'src/app/services/auth.service';
import { SigninRequest } from '../models/signinRequest.model';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AppFacade } from 'src/app/services/appFacade.service';
import { Volume } from '../models/volume.model';
import { Cart } from '../models/cart.model';
import { ProductSummaryComponent } from '../product-summary/product-summary.component';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-product-details',
  templateUrl: './productDetails.component.html',
  styleUrls: ['./productDetails.component.css'],
})
export class ProductDetailsComponent implements OnInit {
  product: IProduct | null = null;
  selectedVolume: Volume | null = null;
  userId: number | null = null;

  cartItem: Cart = {
    id: 0,
    userId: 0,
    productId: 0,
    quantity: 1,
    product: {
      id: 0,
      name: '',
      description: '',
      type: ProductEnum.HAIR,
      stockStatus: '',
      benefits: '',
      usageTips: '',
      ingredients: '',
      characteristics: '',
      image: '',
      volumes: [],
      basePrice: 0,
    },
    selectedVolume: null, // Cela est valide maintenant
  };

  limitReached = false;
  stockMessage: string = '';
  private destroyRef = inject(DestroyRef);
  ProductType = ProductType;

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

  // Load product details from the facade
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

          // Check if the product has volumes
          if (
            product.type === this.ProductType.HAIR &&
            product.volumes?.length
          ) {
            this.selectedVolume = { ...product.volumes[0] }; // Select the first volume
            this.cartItem.selectedVolume = { ...this.selectedVolume }; // Initialize in cartItem
          }

          this.updateStockMessage(product.stockStatus);
        } else {
          console.error('Product not found');
        }
      });
  }

  // Load current user information
  loadCurrentUser(): void {
    this.authService
      .getCurrentUser()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (user: SigninRequest | null) => {
          this.userId = user ? user.id : null;
        },
        error: (error) => {
          console.error('Error fetching user:', error);
        },
      });
  }

  // Update stock message based on stock status
  updateStockMessage(stockStatus: string): void {
    switch (stockStatus) {
      case 'AVAILABLE':
        this.stockMessage = 'Le produit est disponible.';
        break;
      case 'OUT_OF_STOCK':
        this.stockMessage = 'Le produit est actuellement en rupture de stock.';
        break;
      default:
        this.stockMessage = 'Le statut du stock du produit est inconnu.';
    }
  }

  // Handle volume change event
  onVolumeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const selectedValue = target?.value;

    if (selectedValue && this.product?.volumes) {
      const volume = this.product.volumes.find(
        (vol) => vol.volume.toString() === selectedValue
      );

      if (volume) {
        this.selectedVolume = { ...volume }; // Clone to avoid direct reference
        this.cartItem.selectedVolume = { ...this.selectedVolume }; // Update cartItem
      } else {
        this.selectedVolume = null; // Reset if no volume found
      }
    } else {
      this.selectedVolume = null; // Reset if invalid selection
    }
  }

  // Increment quantity
  incrementQuantity(): void {
    if (this.cartItem.quantity < 10) {
      this.cartItem.quantity++;
    } else {
      this.limitReached = true; // Set limit reached flag
    }
  }

  // Decrement quantity
  decrementQuantity(): void {
    if (this.cartItem.quantity > 1) {
      this.cartItem.quantity--;
      this.limitReached = false; // Reset limit reached flag
    }
  }

  // Add to cart function
  addToCart(): void {
    if (!this.userId) {
      alert('Vous devez être connecté pour ajouter des articles au panier.');
      return;
    }

    if (!this.product) {
      alert("Le produit que vous essayez d'ajouter n'est pas disponible.");
      return;
    }

    if (this.product.type === this.ProductType.HAIR && !this.selectedVolume) {
      alert("Veuillez sélectionner un volume avant d'ajouter au panier.");
      return;
    }

    this.cartItem.userId = this.userId;
    this.cartItem.productId = this.product.id;
    this.cartItem.selectedVolume = this.selectedVolume
      ? { ...this.selectedVolume }
      : null;

    // Calculate subtotal
    this.cartItem.subTotal = this.cartItem.selectedVolume
      ? this.cartItem.selectedVolume.price * this.cartItem.quantity
      : (this.product.basePrice || 0) * this.cartItem.quantity;

    this.appFacade
      .addToCart(this.cartItem)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.openProductSummaryDialog();
          this.resetCartItem();
        },
        error: (error) => {
          alert(
            error.error?.message ||
              "Une erreur est survenue lors de l'ajout au panier."
          );
        },
      });
  }

  // Open product summary dialog
  openProductSummaryDialog(): void {
    const dialogRef = this.dialog.open(ProductSummaryComponent, {
      width: '600px',
      height: '900px',
      data: {
        product: this.product,
        cart: this.cartItem,
        quantity: this.cartItem.quantity,
        subTotal: this.cartItem.subTotal,
        selectedVolume: this.cartItem.selectedVolume?.volume || null,
      },
    });

    dialogRef.afterClosed().subscribe(() => {
      console.log('Le dialogue a été fermé');
    });
  }

  // Reset cart item
  private resetCartItem(): void {
    this.cartItem = {
      id: 0,
      userId: 0,
      productId: 0,
      quantity: 1,
      product: {
        id: 0,
        name: '',
        description: '',
        type: this.ProductType.HAIR,
        stockStatus: '',
        benefits: '',
        usageTips: '',
        ingredients: '',
        characteristics: '',
        image: '',
        volumes: [],
        basePrice: 0,
      },
      selectedVolume: null,
    };
    this.selectedVolume = null;
  }
}
