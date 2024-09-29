import { Component, OnInit, inject } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';
import { FavoriteService } from 'src/app/services/favorite.service';
import { Favorite } from '../models/favorite.model';
import { ProductService } from 'src/app/services/product.service';
import { IProduct, ProductType } from '../models/i-product.model';
import { forkJoin } from 'rxjs';
import { switchMap, map } from 'rxjs/operators';
import { Cart } from '../models/cart.model';
import { CartService } from 'src/app/services/cart.service';
import { MatDialog } from '@angular/material/dialog';
import { ProductSummaryComponent } from '../product-summary/product-summary.component';
import { Router } from '@angular/router';
import { DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AppFacade } from 'src/app/services/appFacade.service';
import { IProductVolume } from '../models/IProductVolume.model';

@Component({
  selector: 'app-favorite',
  templateUrl: './favorite.component.html',
  styleUrls: ['./favorite.component.css'],
})
export class FavoriteComponent implements OnInit {
  favorites: Favorite[] = [];
  userId!: number;
  product: IProduct | null = null;
  selectedVolume: IProductVolume | null = null;
  showModal = false;
  confirmedProductId!: number;
  private destroyRef = inject(DestroyRef);
  ProductType = ProductType;

  constructor(
    private authService: AuthService,
    private appFacade: AppFacade,
    private dialog: MatDialog,
    private router: Router
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
              });
              return favorites;
            })
          );
        })
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
      next: (response) => {
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
    product: IProduct,
    selectedVolume: IProductVolume | null
  ): void {
    const cartItem: Cart = {
      id: 0,
      userId: this.userId,
      productId: product.id,
      quantity: 1,
      product: product,
      // Si le produit est de type FACE, on ne passe pas de volume
      selectedVolume:
        product.type === ProductType.FACE
          ? undefined
          : selectedVolume || { id: 0, volume: 0, price: 0 },
    };

    // Calcul du sous-total
    const subTotal =
      product.type === ProductType.FACE
        ? product.basePrice * cartItem.quantity
        : (cartItem.selectedVolume?.price || 0) * cartItem.quantity;

    this.appFacade.cartService.addToCart(cartItem).subscribe({
      next: () => {
        const dialogRef = this.dialog.open(ProductSummaryComponent, {
          width: '60%',
          height: '80%',
          data: {
            product: product,
            quantity: cartItem.quantity,
            // On passe selectedVolume uniquement si c'est un produit de type HAIR
            selectedVolume:
              product.type === ProductType.HAIR
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
