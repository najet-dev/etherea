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
  stockMessage: string = '';
  previousStockMessage: string = ''; // Add previous message
  showError: boolean = false;
  private destroyRef = inject(DestroyRef);
  ProductType = ProductType;
  quantityWarning: boolean = false;
  quantityExceeded: boolean = false; // Indicates whether the stock limit has been exceeded
  limitReached: boolean = false; // Indicates whether the maximum limit of 10 products has been reached
  showDetailPopup = false;

  cartItems: Cart = {
    id: 0,
    userId: 0,
    product: {
      id: 0,
      name: '',
      description: '',
      type: ProductType.FACE,
      stockQuantity: 0,
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

            // Manage volumes
            if (this.product.volumes?.length) {
              this.selectedVolume = this.product.volumes[0];
              this.cartItems.selectedVolume = { ...this.selectedVolume };
              this.cartItems.subTotal =
                this.cartItems.quantity * this.selectedVolume.price;
            }
          } else if (this.productTypeService.isFaceProduct(product)) {
            this.product = product as FaceProduct;
          }

          this.cartItems.productId = product.id;
          this.cartItems.product = { ...product };
          this.updateStockMessage();
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

  onVolumeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const selectedVolumeId = target?.value;

    if (this.product?.type === ProductType.HAIR && this.product.volumes) {
      const selectedVolume = this.product.volumes.find(
        (volume) => volume.id.toString() === selectedVolumeId
      );

      if (selectedVolume) {
        this.selectedVolume = selectedVolume;
        this.cartItems.selectedVolume = selectedVolume;
        this.cartItems.subTotal =
          this.cartItems.quantity * selectedVolume.price;
      }
    }
  }

  incrementQuantity(): void {
    if (
      this.cartItems.quantity < this.cartItems.product.stockQuantity &&
      this.cartItems.quantity < 10
    ) {
      this.cartItems.quantity++;
      this.updateStockMessage();
    } else {
      console.warn('Limite atteinte ou stock insuffisant');
      this.quantityExceeded = true;
      this.updateStockMessage();
    }
  }

  decrementQuantity(): void {
    if (this.cartItems.quantity > 1) {
      this.cartItems.quantity--;
      this.updateStockMessage();
    }
  }
  updateStockMessage(): void {
    const remainingStock =
      this.cartItems.product.stockQuantity - this.cartItems.quantity;

    let newStockMessage: string;

    if (remainingStock > 5) {
      newStockMessage = 'Produit disponible';
      this.quantityWarning = false;
      this.quantityExceeded = false;
    } else if (remainingStock > 0 && remainingStock <= 5) {
      newStockMessage = `Attention : il ne reste que ${remainingStock} produits en stock !`;
      this.quantityWarning = true;
      this.quantityExceeded = false;
    } else if (remainingStock <= 0) {
      newStockMessage = 'Le produit est en rupture de stock';
      this.quantityWarning = false;
      this.quantityExceeded = true;
    } else {
      newStockMessage = '';
    }

    // Update stock message only if necessary
    if (this.stockMessage !== newStockMessage) {
      this.stockMessage = newStockMessage;
    }
  }

  addToCart(): void {
    if (
      this.cartItems.quantity > this.cartItems.product.stockQuantity ||
      this.cartItems.product.stockQuantity === 0
    ) {
      alert("Impossible d'ajouter ce produit au panier.");
      return;
    }
    if (!this.userId) {
      alert('Vous devez être connecté pour ajouter des articles au panier.');
      return;
    }

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

    if (this.product && this.product.stockStatus === 'OUT_OF_STOCK') {
      const errorMessage = `Stock insuffisant pour le produit ${this.product.name}.`;

      if (this.stockMessage !== errorMessage) {
        this.stockMessage = errorMessage;
        this.showError = true;
      }
      return;
    }

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

    this.cartItems.subTotal = subTotal;
    this.cartItems.userId = this.userId;

    if (
      this.product &&
      this.productTypeService.isHairProduct(this.product) &&
      this.selectedVolume
    ) {
      this.cartItems.selectedVolume = this.selectedVolume;
    } else {
      this.cartItems.selectedVolume = undefined;
    }

    this.appFacade
      .addToCart(this.cartItems)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          console.log('Produit ajouté au panier:', this.cartItems);
          this.openProductSummaryDialog();
          this.resetCartItem();
          this.showError = false;
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
        stockQuantity: 0,
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
  //Modal
  toggleDetailPopup() {
    this.showDetailPopup = !this.showDetailPopup;
  }
}
