import { Component, OnInit, inject } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';
import { FavoriteService } from 'src/app/services/favorite.service';
import { Favorite } from '../models/favorite.model';
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
        console.log('Favorite removed:', response);
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

  openProductPopup(product: IProduct): void {
    // Suppose you want to add the first volume to the cart as an example
    const selectedVolume = product.volumes ? product.volumes[0] : null;

    if (selectedVolume) {
      const cartItem: Cart = {
        id: 0,
        userId: this.userId,
        productId: product.id,
        quantity: 1,
        product: product,
        selectedVolume: selectedVolume,
      };

      this.appFacade.cartService.addToCart(cartItem).subscribe({
        next: () => {
          const dialogRef = this.dialog.open(ProductSummaryComponent, {
            width: '60%',
            height: '80%',
            data: {
              product: product,
              quantity: cartItem.quantity,
              subTotal: cartItem.quantity * (selectedVolume.price || 0),
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
    } else {
      console.error('No volume selected for product');
    }
  }
}
