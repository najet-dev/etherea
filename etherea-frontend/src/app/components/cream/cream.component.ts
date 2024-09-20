import { Component, OnDestroy, OnInit } from '@angular/core';
import { IProduct } from '../models/i-product';
import { Observable, of } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';
import { ProductService } from 'src/app/services/product.service';
import { FavoriteService } from 'src/app/services/favorite.service';
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';
import { DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AppFacade } from 'src/app/services/appFacade.service';
import { ProductType } from '../models/i-product'; // Assurez-vous d'importer l'énumération ProductType

@Component({
  selector: 'app-day-cream',
  templateUrl: './cream.component.html',
  styleUrls: ['./cream.component.css'],
})
export class CreamComponent implements OnInit {
  products$: Observable<IProduct[]> = new Observable<IProduct[]>();
  userId: number | null = null;
  private destroyRef = inject(DestroyRef); // Inject DestroyRef

  constructor(
    private productService: ProductService,
    private authService: AuthService,
    private favoriteService: FavoriteService,
    private appFacade: AppFacade,
    private router: Router
  ) {
    this.authService
      .getCurrentUser()
      .pipe(
        tap((user) => {
          this.userId = user ? user.id : null;
          this.loadProducts(); // Load products after determining user ID
        }),
        takeUntilDestroyed(this.destroyRef) // Use takeUntilDestroyed
      )
      .subscribe();
  }
  ngOnInit(): void {
    throw new Error('Method not implemented.');
  }

  loadProducts(): void {
    const productType = ProductType.FACE; // Utiliser l'énumération ProductType
    const page = 0; // Numéro de la page
    const size = 10; // Taille de la page

    this.products$ = this.appFacade
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
    this.appFacade.toggleFavorite(product);
  }
}
