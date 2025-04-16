import {
  Component,
  OnDestroy,
  OnInit,
  inject,
  DestroyRef,
} from '@angular/core';
import { Product } from '../models/product.model';
import { Observable, of } from 'rxjs';
import { catchError, switchMap, tap, map } from 'rxjs/operators';
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AppFacade } from 'src/app/services/appFacade.service';
import { ProductTypeService } from 'src/app/services/product-type.service';
import { SearchService } from 'src/app/services/search.service';

@Component({
  selector: 'app-day-cream',
  templateUrl: './cream.component.html',
  styleUrls: ['./cream.component.css'],
})
export class CreamComponent implements OnInit {
  products$: Observable<Product[]> = of([]);
  userId: number | null = null;
  currentPage: number = 0;
  totalPages: number = 1;
  pageSize: number = 10;

  private destroyRef = inject(DestroyRef);

  constructor(
    private authService: AuthService,
    private appFacade: AppFacade,
    private router: Router,
    public productTypeService: ProductTypeService,
    private searchService: SearchService
  ) {}

  ngOnInit(): void {
    this.authService
      .getCurrentUser()
      .pipe(
        tap((user) => {
          this.userId = user ? user.id : null;
          this.loadProducts();
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe();
  }

  loadProducts(): void {
    const productType = 'FACE';

    this.products$ = this.appFacade
      .getProductsByType(productType, this.currentPage, this.pageSize)
      .pipe(
        tap((response) => {
          this.totalPages = response.totalPages;
        }),
        map((response) => response.content),
        switchMap((products: Product[]) => {
          const faceProducts = products.filter((product) =>
            this.productTypeService.isFaceProduct(product)
          );

          if (this.userId !== null) {
            return this.appFacade.productsFavorites(faceProducts);
          }

          return of(faceProducts);
        }),
        catchError((error) => {
          console.error('Error fetching products:', error);
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

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadProducts();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadProducts();
    }
  }
}
