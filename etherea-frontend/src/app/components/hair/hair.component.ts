import { Component, OnInit, OnDestroy } from '@angular/core';
import { ProductService } from 'src/app/services/product.service';
import { Product } from '../models/Product.model';
import { Observable, of } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';
import { AuthService } from 'src/app/services/auth.service';
import { FavoriteService } from 'src/app/services/favorite.service';
import { Router } from '@angular/router';
import { DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AppFacade } from 'src/app/services/appFacade.service';
import { ProductTypeService } from 'src/app/services/product-type.service';
import { HairProduct } from '../models';

@Component({
  selector: 'app-hair',
  templateUrl: './hair.component.html',
  styleUrls: ['./hair.component.css'],
})
export class HairComponent implements OnInit {
  products$: Observable<Product[]> = new Observable<Product[]>();
  userId: number | null = null;
  private destroyRef = inject(DestroyRef); // Inject DestroyRef

  constructor(
    private authService: AuthService,
    private appFacade: AppFacade,
    private router: Router,
    public productTypeService: ProductTypeService
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

  loadProducts(): void {
    const productType = 'HAIR';
    const page = 0;
    const size = 10;

    this.products$ = this.appFacade
      .getProductsByType(productType, page, size)
      .pipe(
        tap((products: Product[]) => {
          console.log('Products received:', products);
        }),
        switchMap((products: Product[]) => {
          const hairProducts = products.filter((product) =>
            this.productTypeService.isHairProduct(product)
          );
          console.log('Hair Products:', hairProducts); // Log des produits filtrés
          if (this.userId !== null) {
            return this.appFacade.productsFavorites(hairProducts);
          }
          return of(hairProducts);
        }),
        catchError((error) => {
          console.error('Error fetching products:', error);
          return of([] as HairProduct[]);
        })
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
