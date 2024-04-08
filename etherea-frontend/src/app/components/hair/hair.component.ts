import { Component, OnDestroy } from '@angular/core';
import { ProductService } from 'src/app/services/product.service';
import { IProduct } from '../models/i-product';
import { Observable, Subject, catchError, takeUntil } from 'rxjs';

@Component({
  selector: 'app-hair',
  templateUrl: './hair.component.html',
  styleUrls: ['./hair.component.css'],
})
export class HairComponent implements OnDestroy {
  products$: Observable<IProduct[]> = new Observable<IProduct[]>();
  private destroy$ = new Subject<void>();

  constructor(private productService: ProductService) {
    this.loadProducts();
  }

  private loadProducts(): void {
    const page = 0; // Numéro de la page
    const size = 10; // Taille de la page
    this.products$ = this.productService
      .getProducts('HAIR', page, size) // Utilisez 'hair' comme type pour les produits des cheveux
      .pipe(
        catchError((error) => {
          console.error('Error fetching hair products:', error);
          console.error(
            'Failed to load hair products. Please try again later.'
          );
          return [];
        }),
        takeUntil(this.destroy$)
      );
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
