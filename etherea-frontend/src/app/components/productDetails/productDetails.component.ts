import { Component, OnInit } from '@angular/core';
import { switchMap, catchError, of } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AuthService } from 'src/app/services/auth.service';
import { AppFacade } from 'src/app/services/appFacade.service';
import { DestroyRef, inject } from '@angular/core';
import { SigninRequest } from '../models/signinRequest.model';
import { ProductSummaryComponent } from '../product-summary/product-summary.component';
import { ProductType } from '../models/ProductType.enum';
import { ProductTypeService } from 'src/app/services/product-type.service';
import { Cart } from '../models/cart.model';
import { HairProduct } from '../models/HairProduct.model';
import { FaceProduct } from '../models/FaceProduct.model';
import { ProductVolume } from '../models/ProductVolume.model';

@Component({
  selector: 'app-product-details',
  templateUrl: './productDetails.component.html',
  styleUrls: ['./productDetails.component.css'],
})
export class ProductDetailsComponent implements OnInit {
  product: HairProduct | FaceProduct | null = null;
  selectedVolume: ProductVolume | null = null;
  userId: number | null = null;

  cartItems: Cart = {
    id: 0,
    userId: 0,
    product: {
      id: 0,
      name: '',
      description: '',
      type: ProductType.FACE,
      stockStatus: '',
      benefits: '',
      usageTips: '',
      ingredients: '',
      characteristics: '',
      image: '',
      isFavorite: false,
    },
    hairProduct: null,
    faceProduct: null,
    productId: 0,
    quantity: 1,
    subTotal: 0,
    selectedVolume: {
      id: 0,
      volume: 0,
      price: 0,
    },
  };

  limitReached = false;
  stockMessage: string = '';
  private destroyRef = inject(DestroyRef);
  ProductType = ProductType;

  constructor(
    private route: ActivatedRoute,
    private appFacade: AppFacade,
    private dialog: MatDialog,
    private authService: AuthService,
    public productTypeService: ProductTypeService
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
          if (this.productTypeService.isHairProduct(product)) {
            this.product = product as HairProduct;

            // Vérifier si le produit a des volumes disponibles
            if (this.product.volumes && this.product.volumes.length > 0) {
              // Sélectionner automatiquement le premier volume
              this.selectedVolume = this.product.volumes[0];
              this.cartItems.selectedVolume = { ...this.selectedVolume };

              // Mettre à jour le sous-total en fonction du premier volume
              this.cartItems.subTotal =
                this.cartItems.quantity * this.selectedVolume.price;
            }
          } else if (this.productTypeService.isFaceProduct(product)) {
            this.product = product as FaceProduct;
          }

          this.cartItems.productId = product.id;
          this.cartItems.product = { ...product };
          this.updateStockMessage(product.stockStatus);
        } else {
          console.error('Product not found');
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
          console.error('Error fetching user:', error);
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

    if (this.product?.type === 'HAIR' && this.product.volumes) {
      const volume = this.product.volumes.find(
        (vol: ProductVolume) => vol.volume.toString() === selectedValue
      );

      if (volume) {
        this.selectedVolume = volume; // Directement assigner le volume
        this.cartItems.selectedVolume = volume; // Mettre à jour le volume dans le panier
        this.cartItems.subTotal = this.cartItems.quantity * volume.price; // Mettre à jour le sous-total
      } else {
        this.selectedVolume = null; // Réinitialiser si aucun volume n'est trouvé
      }
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
    // Vérifiez que l'utilisateur est connecté
    if (!this.userId) {
      alert('Vous devez être connecté pour ajouter des articles au panier.');
      return;
    }

    // Vérifiez que le produit est de type Hair et que le volume est sélectionné
    if (
      this.product &&
      this.productTypeService.isHairProduct(this.product) &&
      !this.selectedVolume
    ) {
      alert(
        "Veuillez sélectionner un volume avant d'ajouter un produit capillaire au panier."
      );
      return;
    }
    // Calculer le sous-total selon le type de produit
    let subTotal = 0;

    if (this.product) {
      if (
        this.productTypeService.isHairProduct(this.product) &&
        this.selectedVolume
      ) {
        subTotal = this.cartItems.quantity * this.selectedVolume.price;
      } else if (this.productTypeService.isFaceProduct(this.product)) {
        subTotal = this.cartItems.quantity * this.product.basePrice;
      }
    }

    // Mettre à jour les informations du panier
    this.cartItems.subTotal = subTotal;
    this.cartItems.userId = this.userId;

    // Affecter le volume sélectionné pour les produits HAIR
    if (
      this.product &&
      this.productTypeService.isHairProduct(this.product) &&
      this.selectedVolume
    ) {
      this.cartItems.selectedVolume = this.selectedVolume;
    } else {
      this.cartItems.selectedVolume = undefined;
    }

    // Appel au service pour ajouter au panier
    this.appFacade
      .addToCart(this.cartItems)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          console.log('Produit ajouté au panier:', this.cartItems);

          this.openProductSummaryDialog();

          // Réinitialiser les informations du panier
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
        selectedVolume:
          this.product?.type === 'HAIR'
            ? this.cartItems.selectedVolume
            : undefined,
      },
    });

    dialogRef.afterClosed().subscribe(() => {
      console.log('The dialog was closed');
    });
  }
  private resetCartItem(): void {
    this.cartItems = {
      id: 0,
      userId: 0,
      product: {
        id: 0,
        name: '',
        description: '',
        type: ProductType.FACE,
        stockStatus: '',
        benefits: '',
        usageTips: '',
        ingredients: '',
        characteristics: '',
        image: '',
        isFavorite: false,
      },
      hairProduct: null,
      faceProduct: null,
      productId: 0,
      quantity: 1,
      subTotal: 0,
      selectedVolume: undefined,
    };
  }
}
