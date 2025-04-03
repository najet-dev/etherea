import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';
import { AppFacade } from 'src/app/services/appFacade.service';
import { ProductTypeService } from 'src/app/services/product-type.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Product } from '../models/product.model';
import { ProductService } from 'src/app/services/product.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  products$: Observable<Product[]> = new Observable<Product[]>();
  userId: number | null = null;
  private destroyRef = inject(DestroyRef);

  constructor(
    private authService: AuthService,
    private appFacade: AppFacade,
    private productService: ProductService,
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
    this.products$ = this.appFacade.getAllProducts().pipe(
      switchMap((products) =>
        this.appFacade.productsFavorites(products.content)
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
