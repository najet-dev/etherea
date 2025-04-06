import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Product } from 'src/app/components/models/product.model';
import { ProductTypeService } from 'src/app/services/product-type.service';
import { Router } from '@angular/router';
import { AppFacade } from 'src/app/services/appFacade.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-new-products',
  templateUrl: './new-products.component.html',
  styleUrls: ['./new-products.component.css'],
})
export class NewProductsComponent {
  newProducts: Product[] = [];
  userId: number | null = null;
  currentPage: number = 0;
  totalPages: number = 1;
  pageSize: number = 10;
  searchQuery: string = '';
  private destroyRef = inject(DestroyRef);

  constructor(
    private appFacade: AppFacade,
    private router: Router,
    public productTypeService: ProductTypeService
  ) {}

  ngOnInit(): void {
    this.loadNewProducts();
  }

  loadNewProducts(): void {
    this.appFacade
      .getNewProducts(this.currentPage, this.pageSize)
      .pipe(
        tap((response) => {
          this.newProducts = response.content;
          this.totalPages = response.totalPages;
        }),
        catchError((error) => {
          console.error('Erreur lors de la récupération des produits:', error);
          this.newProducts = [];
          return of([]);
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe();
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadNewProducts();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadNewProducts();
    }
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
