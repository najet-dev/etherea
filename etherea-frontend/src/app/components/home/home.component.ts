import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, switchMap, tap, map } from 'rxjs/operators';
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';
import { AppFacade } from 'src/app/services/appFacade.service';
import { ProductTypeService } from 'src/app/services/product-type.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Product } from '../models/product.model';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent {
  products$: Observable<Product[]> = of([]);
  userId: number | null = null;
  private destroyRef = inject(DestroyRef);

  constructor(
    private authService: AuthService,
    private appFacade: AppFacade,
    private router: Router,
    public productTypeService: ProductTypeService
  ) {}

  ngOnInit(): void {
    this.loadProducts();

    this.authService
      .getCurrentUser()
      .pipe(
        tap((user) => (this.userId = user ? user.id : null)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe();
  }

  loadProducts(): void {
    this.products$ = this.appFacade.getAllProducts(0, 4).pipe(
      map((response) => response.content), // Extraire le tableau de produits
      switchMap((products) =>
        this.userId !== null
          ? this.appFacade.productsFavorites(products)
          : of(products)
      ),
      catchError((error) => {
        console.error('Erreur lors du chargement des produits :', error);
        return of([]);
      }),
      takeUntilDestroyed(this.destroyRef)
    );
  }

  handleFavoriteClick(product: Product): void {
    if (this.userId === null) {
      this.router.navigate(['/signin']);
    } else {
      this.toggleFavorite(product);
    }
  }

  toggleFavorite(product: Product): void {
    this.appFacade.toggleFavorite(product);
  }
}
