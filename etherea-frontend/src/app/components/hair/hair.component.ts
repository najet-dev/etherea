import { Component, OnDestroy, OnInit } from '@angular/core';
import { ProductService } from 'src/app/services/product.service';
import { IProduct } from '../models/i-product';
import {
  Observable,
  Subject,
  catchError,
  of,
  switchMap,
  takeUntil,
  tap,
} from 'rxjs';
import { AuthService } from 'src/app/services/auth.service';
import { FavoriteService } from 'src/app/services/favorite.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-hair',
  templateUrl: './hair.component.html',
  styleUrls: ['./hair.component.css'],
})
export class HairComponent implements OnInit {
  products$: Observable<IProduct[]> = new Observable<IProduct[]>();
  private destroy$ = new Subject<void>();
  userId: number | null = null;

  constructor(
    private productService: ProductService,
    private authService: AuthService,
    private favoriteService: FavoriteService,
    private router: Router
  ) {
    this.authService
      .getCurrentUser()
      .pipe(
        tap((user) => (this.userId = user ? user.id : null)),
        tap(() => this.loadProducts()) // Load products after determining user ID
      )
      .subscribe();
  }
  ngOnInit(): void {
    throw new Error('Method not implemented.');
  }

  private loadProducts(): void {
    const productType = 'HAIR';
    const page = 0; // Numéro de la page
    const size = 10; // Taille de la page

    this.products$ = this.productService
      .getProductsByType(productType, page, size)
      .pipe(
        switchMap((products) => {
          if (this.userId !== null) {
            return this.favoriteService.productsFavorites(products);
          }
          return of(products);
        }),
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
