import { Component, OnInit } from '@angular/core';
import { ProductService } from 'src/app/services/product.service';
import { IProduct, ProductType } from '../models/i-product'; // Importez ProductType ici
import { Observable, of } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';
import { DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AppFacade } from 'src/app/services/appFacade.service';

@Component({
  selector: 'app-hair',
  templateUrl: './hair.component.html',
  styleUrls: ['./hair.component.css'],
})
export class HairComponent implements OnInit {
  products$: Observable<IProduct[]> = new Observable<IProduct[]>();
  userId: number | null = null;
  private destroyRef = inject(DestroyRef); // Inject DestroyRef

  constructor(
    private authService: AuthService,
    private appFacade: AppFacade,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService
      .getCurrentUser()
      .pipe(
        tap((user) => (this.userId = user ? user.id : null)),
        tap(() => this.loadProducts()),
        takeUntilDestroyed(this.destroyRef) // Use takeUntilDestroyed
      )
      .subscribe();
  }

  private loadProducts(): void {
    const productType: ProductType = ProductType.HAIR; // Utilisez l'énumération ProductType ici
    const page = 0; // Numéro de la page
    const size = 10; // Taille de la page

    this.products$ = this.appFacade
      .getProductsByType(productType, page, size)
      .pipe(
        switchMap((products) => {
          if (this.userId !== null) {
            return this.appFacade.productsFavorites(products);
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
