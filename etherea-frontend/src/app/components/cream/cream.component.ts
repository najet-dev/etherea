import { Component, OnDestroy } from '@angular/core';
import { IProduct } from '../models/i-product';
import { Observable, Subject, catchError, takeUntil } from 'rxjs';
import { ProductService } from 'src/app/services/product.service';

@Component({
  selector: 'app-day-cream',
  templateUrl: './cream.component.html',
  styleUrls: ['./cream.component.css'],
})
export class CreamComponent implements OnDestroy {
  products$: Observable<IProduct[]> = new Observable<IProduct[]>();
  private destroy$ = new Subject<void>();

  constructor(private productService: ProductService) {
    this.loadProducts();
  }

  private loadProducts(): void {
    // Ajouter le paramètre limit pour spécifier le nombre de produits
    this.products$ = this.productService.getProducts().pipe(
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
}
