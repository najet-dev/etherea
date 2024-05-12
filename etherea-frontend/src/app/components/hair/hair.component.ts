import { Component, OnDestroy } from '@angular/core';
import { ProductService } from 'src/app/services/product.service';
import { IProduct } from '../models/i-product';
import {
  Observable,
  Subject,
  catchError,
  map,
  switchMap,
  takeUntil,
  tap,
} from 'rxjs';
import { AuthService } from 'src/app/services/auth.service';
import { FavoriteService } from 'src/app/services/favorite.service';

@Component({
  selector: 'app-hair',
  templateUrl: './hair.component.html',
  styleUrls: ['./hair.component.css'],
})
export class HairComponent implements OnDestroy {
  products$: Observable<IProduct[]> = new Observable<IProduct[]>();
  private destroy$ = new Subject<void>();
  userId: number | null = null;

  constructor(
    private productService: ProductService,
    private authService: AuthService,
    private favoriteService: FavoriteService
  ) {
    this.authService
      .getCurrentUser()
      .pipe(tap((user) => (this.userId = user ? user.id : null)))
      .subscribe();
    this.loadProducts();
  }

  private loadProducts(): void {
    const productType = 'HAIR';
    const page = 0; // Numéro de la page
    const size = 10; // Taille de la page
    if (this.userId !== null) {
      this.products$ = this.productService
        .getProductsByType(productType, page, size)
        .pipe(
          switchMap((products) =>
            this.favoriteService.getUserFavorites(this.userId!).pipe(
              map((favorites) =>
                products.map((product) => ({
                  ...product,
                  isFavorite: favorites.some(
                    (fav) => fav.productId === product.id
                  ),
                }))
              )
            )
          ),
          catchError((error) => {
            console.error(
              'Erreur lors de la récupération des produits :',
              error
            );
            console.error(
              'Échec du chargement des produits. Veuillez réessayer plus tard.'
            );
            return [];
          }),
          takeUntil(this.destroy$)
        );
    } else {
      console.error('UserId est null');
    }
  }
  toggleFavorite(product: IProduct): void {
    this.favoriteService.toggleFavorite(product);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
