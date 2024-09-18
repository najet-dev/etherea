import { Component, OnInit, DestroyRef, inject } from '@angular/core';
import { switchMap, catchError, tap, of } from 'rxjs';
import { IProduct } from '../models/i-product';
import { ProductService } from 'src/app/services/product.service';
import { ActivatedRoute } from '@angular/router';
import { CartService } from 'src/app/services/cart.service';
import { Cart } from '../models/cart.model';
import { ProductSummaryComponent } from '../product-summary/product-summary.component';
import { MatDialog } from '@angular/material/dialog';
import { AuthService } from 'src/app/services/auth.service';
import { SigninRequest } from '../models/signinRequest.model';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AppFacade } from 'src/app/services/appFacade.service';
import { Volume } from '../models/volume.model';

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
      id: 0,
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
          this.cartItem.product = { ...product };
          this.updateStockMessage(product.stockStatus);
          this.selectedVolume = product.volumes ? product.volumes[0] : null; // Initialiser le volume sélectionné
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
        this.stockMessage = 'Le produit est disponible.';
        break;
      case 'OUT_OF_STOCK':
        this.stockMessage = 'Le produit est actuellement en rupture de stock.';
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
      } else {
        console.error('Selected volume not found in product volumes.');
      }
    } else {
      console.error(
        'Invalid volume selection or product volumes not available.'
      );
    }
  }

  incrementQuantity(): void {
    if (this.cartItem.quantity < 10) {
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
      console.error('ID utilisateur non disponible.');
      alert('Vous devez être connecté pour ajouter des articles au panier.');
      return;
    }

    if (!this.selectedVolume) {
      console.error('Aucun volume sélectionné.');
      alert("Veuillez sélectionner un volume avant d'ajouter au panier.");
      return;
    }

    this.cartItem.userId = this.userId;
    this.cartItem.selectedVolume = { ...this.selectedVolume };

    this.appFacade
      .addToCart(this.cartItem)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          console.log('Produit ajouté au panier');
          this.openProductSummaryDialog();
          this.resetCartItem();
        },
        error: (error) => {
          console.error('Erreur lors de l’ajout du produit au panier :', error);
          alert(
            error.error?.message ||
              "Une erreur est survenue lors de l'ajout au panier."
          );
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
      productId: 0,
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
        id: 0,
        volume: 0,
        price: 0,
      },
    };
    this.selectedVolume = null;
  }
}
