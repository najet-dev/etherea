import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ProductService } from 'src/app/services/product.service';
import { FavoriteService } from 'src/app/services/favorite.service';
import { IProduct } from '../models/i-product';
import { Observable, of } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';
import { DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  products$: Observable<IProduct[]> = new Observable<IProduct[]>();
  userId: number | null = null;
  private destroyRef = inject(DestroyRef); // Inject DestroyRef

  constructor(
    private productService: ProductService,
    private favoriteService: FavoriteService,
    private authService: AuthService,
    private router: Router
  ) {
    this.loadProducts();
    this.authService
      .getCurrentUser()
      .pipe(
        tap((user) => (this.userId = user ? user.id : null)),
        takeUntilDestroyed(this.destroyRef) // Use takeUntilDestroyed
      )
      .subscribe();
  }
  ngOnInit(): void {
    throw new Error('Method not implemented.');
  }

  loadProducts(): void {
    this.products$ = this.productService.getProducts(12).pipe(
      switchMap((products) => this.favoriteService.productsFavorites(products)),
      catchError((error) => {
        console.error('Error fetching products:', error);
        console.error('Failed to load products. Please try again later.');
        return of([]);
      }),
      takeUntilDestroyed(this.destroyRef) // Use takeUntilDestroyed
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
}
