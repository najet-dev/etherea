import { Component, OnInit, inject, DestroyRef } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';
import { FavoriteService } from 'src/app/services/favorite.service';
import { Favorite } from '../models/favorite.model';
import { Product } from '../models/Product.model';
import { ProductType } from '../models/ProductType.enum';
import { forkJoin } from 'rxjs';
import { switchMap, map } from 'rxjs/operators';
import { Cart } from '../models/cart.model';
import { MatDialog } from '@angular/material/dialog';
import { ProductSummaryComponent } from '../product-summary/product-summary.component';
import { Router } from '@angular/router';
import { AppFacade } from 'src/app/services/appFacade.service';
import { ProductVolume } from '../models/ProductVolume.model';
import { FaceProduct } from '../models/FaceProduct.model';
import { ProductTypeService } from 'src/app/services/product-type.service';
import { HairProduct } from '../models/HairProduct.model';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-favorite',
  templateUrl: './favorite.component.html',
  styleUrls: ['./favorite.component.css'],
})
export class FavoriteComponent implements OnInit {
  favorites: Favorite[] = [];
  userId!: number;
  selectedVolume: ProductVolume | undefined;
  showModal = false;
  confirmedProductId!: number;
  private destroyRef = inject(DestroyRef);

  constructor(
    private authService: AuthService,
    private appFacade: AppFacade,
    private dialog: MatDialog,
    private router: Router,
    public productTypeService: ProductTypeService
  ) {}

  ngOnInit(): void {
    this.authService
      .getCurrentUser()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((user) => {
        if (user && user.id) {
          this.userId = user.id;
          this.loadFavorites();
        }
      });
  }

  loadFavorites(): void {
    this.appFacade
      .getUserFavorites(this.userId)
      .pipe(
        switchMap((favorites) => {
          const productObservables = favorites.map((favorite) =>
            this.appFacade.getProductById(favorite.productId)
          );
          return forkJoin(productObservables).pipe(
            map((products) => {
              favorites.forEach((favorite, index) => {
                favorite.product = products[index];

                // Vérifier si le produit est un HairProduct
                if (this.productTypeService.isHairProduct(favorite.product)) {
                  const hairProduct = favorite.product as HairProduct;

                  // Si le produit a des volumes, sélectionner le premier volume par défaut
                  if (hairProduct.volumes && hairProduct.volumes.length > 0) {
                    this.selectedVolume = hairProduct.volumes[0]; // Sélectionner le premier volume
                  }
                }
              });
              return favorites;
            })
          );
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (favorites) => {
          this.favorites = favorites;
        },
        error: (error) => {
          console.error('Error loading favorites:', error);
        },
      });
  }

  confirmRemoveFavorite(productId: number): void {
    this.confirmedProductId = productId;
    this.showModal = true;
  }

  removeFavorite(productId: number): void {
    this.appFacade.removeFavorite(this.userId, productId).subscribe({
      next: () => {
        this.favorites = this.favorites.filter(
          (favorite) => favorite.productId !== productId
        );
        this.hideModal();
      },
      error: (error) => {
        console.error('Error removing favorite:', error);
      },
    });
  }

  hideModal(): void {
    this.showModal = false;
  }

  openProductPopup(
    product: Product,
    selectedVolume: ProductVolume | undefined
  ): void {
    const cartItem: Cart = {
      id: 0,
      userId: this.userId,
      productId: product.id,
      quantity: 1,
      product: product,
      selectedVolume: this.productTypeService.isHairProduct(product)
        ? selectedVolume
        : undefined,
      hairProduct: this.productTypeService.isHairProduct(product)
        ? (product as HairProduct)
        : null,
      faceProduct: this.productTypeService.isFaceProduct(product)
        ? (product as FaceProduct)
        : null,
    };

    const subTotal = this.productTypeService.isFaceProduct(product)
      ? (product as FaceProduct).basePrice * cartItem.quantity
      : (selectedVolume?.price || 0) * cartItem.quantity;

    this.appFacade.cartService.addToCart(cartItem).subscribe({
      next: () => {
        const dialogRef = this.dialog.open(ProductSummaryComponent, {
          width: '60%',
          height: '80%',
          data: {
            product: product,
            quantity: cartItem.quantity,
            selectedVolume: this.productTypeService.isHairProduct(product)
              ? cartItem.selectedVolume
              : null,
            cart: cartItem,
            subTotal: subTotal,
          },
        });

        dialogRef.afterClosed().subscribe({
          next: (result) => {
            if (result === 'goToCart') {
              this.router.navigateByUrl('/cart');
            }
          },
        });
      },
      error: (error) => {
        console.error('Error adding product to cart:', error);
      },
    });
  }
}
