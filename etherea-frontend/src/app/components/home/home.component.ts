import { Component, OnDestroy } from '@angular/core';
import { ProductService } from 'src/app/services/product.service';
import { FavoriteService } from 'src/app/services/favorite.service';
import { IProduct } from '../models/i-product';
import {
  Observable,
  Subject,
  catchError,
  takeUntil,
  switchMap,
  map,
  tap,
  of,
} from 'rxjs';
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnDestroy {
  products$: Observable<IProduct[]> = new Observable<IProduct[]>();
  private destroy$ = new Subject<void>();
  userId: number | null = null;

  constructor(
    private productService: ProductService,
    private favoriteService: FavoriteService,
    private authService: AuthService,
    private router: Router
  ) {
    this.loadProducts();
    this.authService
      .getCurrentUser()
      .pipe(tap((user) => (this.userId = user ? user.id : null)))
      .subscribe();
  }

  loadProducts(): void {
    this.products$ = this.productService.getProducts(12).pipe(
      switchMap((products) => this.favoriteService.productsFavorites(products)),
      catchError((error) => {
        console.error('Error fetching products:', error);
        console.error('Failed to load products. Please try again later.');
        return of([]);
      }),
      takeUntil(this.destroy$)
    );
  }

  handleFavoriteClick(product: IProduct): void {
    if (this.userId === null) {
      this.router.navigate(['/signin']);
    } else {
      this.toggleFavorite(product);
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
