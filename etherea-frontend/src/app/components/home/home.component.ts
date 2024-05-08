import { Component, OnDestroy } from '@angular/core';
import { ProductService } from 'src/app/services/product.service';
import { FavoriteService } from 'src/app/services/favorite.service';
import { IProduct } from '../models/i-product';
import { Favorite } from '../models/favorite.model';
import { Observable, Subject, catchError, takeUntil } from 'rxjs';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnDestroy {
  products$: Observable<IProduct[]> = new Observable<IProduct[]>();
  private destroy$ = new Subject<void>();
  userId!: number;

  constructor(
    private productService: ProductService,
    private favoriteService: FavoriteService,
    private authService: AuthService
  ) {
    this.loadProducts();
    this.authService.getCurrentUser().subscribe((user) => {
      if (user && user.id) {
        this.userId = user.id;
      }
    });
  }

  private loadProducts(): void {
    this.products$ = this.productService.getProducts(12).pipe(
      catchError((error) => {
        console.error('Error fetching products:', error);
        console.error('Failed to load products. Please try again later.');
        return [];
      }),
      takeUntil(this.destroy$)
    );
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  addToFavorites(productId: number): void {
    this.favoriteService.addFavorite(this.userId, productId).subscribe({
      next: (response: any) => {
        console.log(response);
        if (response && response.message) {
          console.log(response.message);
        }
      },
      error: (error) => {
        console.error('Error adding favorite:', error);
      },
    });
  }
}
