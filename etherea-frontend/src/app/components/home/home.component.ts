import { Component, OnDestroy } from '@angular/core';
import { ProductService } from 'src/app/services/product.service';
import { IProduct } from '../models/i-product';
import { Observable, Subject, catchError, takeUntil } from 'rxjs';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnDestroy {
  products$: Observable<IProduct[]> = new Observable<IProduct[]>();
  private destroy$ = new Subject<void>();

  constructor(private productService: ProductService) {
    this.loadProducts();
  }

  private loadProducts(): void {
    // Ajouter le paramètre limit pour spécifier le nombre de produits
    this.products$ = this.productService.getProducts(6).pipe(
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
