import { Component, OnInit } from '@angular/core';
import { switchMap, catchError, of } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { IProduct } from '../models/i-product.model';
import { Cart } from '../models/cart.model';
import { AuthService } from 'src/app/services/auth.service';
import { AppFacade } from 'src/app/services/appFacade.service';
import { IProductVolume } from '../models/IProductVolume.model';
import { DestroyRef, inject } from '@angular/core';
import { SigninRequest } from '../models/signinRequest.model';
import { ProductSummaryComponent } from '../product-summary/product-summary.component';

@Component({
  selector: 'app-product-details',
  templateUrl: './productDetails.component.html',
  styleUrls: ['./productDetails.component.css'],
})
export class ProductDetailsComponent implements OnInit {
  product: IProduct | null = null;
  selectedVolume: IProductVolume | null = null;
  userId: number | null = null;

  cartItems: Cart = {
    id: 0,
    userId: 0,
    productId: 0,
    quantity: 1,
    product: {
      id: 0,
      name: '',
      description: '',
      type: '',
      basePrice: 0,
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
    subTotal: 0, // Ajout de la propriété subTotal ici
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
        switchMap((params) => this.appFacade.getProductById(params['id'])),
        catchError((error) => {
          console.error('Error fetching product:', error);
          return of(null);
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe((product) => {
        if (product) {
          this.product = product;
          this.cartItems.productId = product.id;
          this.cartItems.product = { ...product };
          this.updateStockMessage(product.stockStatus);

          if (product.type === ProductType.HAIR && product.volumes?.length) {
            this.selectedVolume = product.volumes[0];
          } else {
            this.selectedVolume = null;
          }
        }
      });
  }

  loadCurrentUser(): void {
    this.authService
      .getCurrentUser()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (user: SigninRequest | null) => {
          if (user) {
            console.log('Utilisateur connecté:', user);
            this.userId = user.id;
          } else {
            console.log('Aucun utilisateur connecté');
            this.userId = null;
          }
        },
        error: (error) => {
          console.error(
            'Erreur lors de la récupération de l’utilisateur:',
            error
          );
        },
      });
  }

  updateStockMessage(stockStatus: string): void {
    this.stockMessage =
      stockStatus === 'AVAILABLE'
        ? `Le produit est disponible.`
        : stockStatus === 'OUT_OF_STOCK'
        ? `Le produit est actuellement en rupture de stock.`
        : 'Le statut du stock du produit est inconnu.';
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
        // Mettre à jour le sous-total lors du changement de volume
        this.cartItems.subTotal =
          this.cartItems.quantity * this.selectedVolume.price;
      } else {
        console.error('Selected volume not found in product volumes.');
      }
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
        console.error(
          'Volume sélectionné introuvable dans les volumes du produit.'
        );
      }
    } else {
      console.error(
        'Sélection de volume invalide ou volumes du produit non disponibles.'
      );
    }
  }

  incrementQuantity(): void {
    if (this.cartItems.quantity < 10) {
      this.cartItems.quantity++;
    } else {
      this.limitReached = true;
    }
  }

  decrementQuantity(): void {
    if (this.cartItems.quantity > 1) {
      this.cartItems.quantity--;
    }
  }

  addToCart(): void {
    if (!this.userId) {
      alert('Vous devez être connecté pour ajouter des articles au panier.');
      return;
    }

    // Vérifiez que le volume est sélectionné pour les produits HAIR
    if (this.product?.type === 'HAIR' && !this.selectedVolume) {
      alert("Veuillez sélectionner un volume avant d'ajouter au panier.");
      return;
    }

    // Calculer le sous-total selon le type de produit
    const subTotal =
      this.product?.type === 'HAIR' && this.selectedVolume
        ? this.cartItems.quantity * this.selectedVolume.price
        : this.product?.type === 'FACE'
        ? this.cartItems.quantity * this.product.basePrice
        : 0;

    // Mettez à jour le cartItem avec le volume sélectionné et le sous-total
    this.cartItems.subTotal = subTotal; // Met à jour le sous-total
    this.cartItems.userId = this.userId;

    // Affecter le volume sélectionné uniquement pour les produits HAIR
    if (this.product?.type === 'HAIR' && this.selectedVolume) {
      this.cartItems.selectedVolume = this.selectedVolume; // Assurez-vous que cela est défini
    }

    // Appel au service pour ajouter au panier
    this.appFacade
      .addToCart(this.cartItems)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          console.log('Produit ajouté au panier:', this.cartItems);
          this.openProductSummaryDialog();
          this.resetCartItem();
        },
        error: (error) => {
          console.error("Erreur lors de l'ajout du produit au panier:", error);
        },
      });
  }

  openProductSummaryDialog(): void {
    const dialogRef = this.dialog.open(ProductSummaryComponent, {
      width: '600px',
      height: '900px',
      data: {
        product: this.product,
        cart: this.cartItems,
        quantity: this.cartItems.quantity,
        subTotal: this.cartItems.subTotal,
      },
    });

    dialogRef.afterClosed().subscribe(() => {
      console.log('Le dialogue a été fermé');
    });
  }

  private resetCartItem(): void {
    this.cartItems = {
      id: 0,
      userId: this.userId || 0,
      productId: this.product?.id || 0,
      quantity: 1,
      product: this.product || {
        id: 0,
        name: '',
        description: '',
        type: '',
        basePrice: 0,
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
      subTotal: 0, // Réinitialiser le sous-total
    };
    this.selectedVolume = null;
  }
}
