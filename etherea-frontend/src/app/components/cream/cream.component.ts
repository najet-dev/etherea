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
    const productType = 'FACE'; // Type de produit pour le visage
    const page = 0; // Numéro de la page
    const size = 10; // Taille de la page
    this.products$ = this.productService
      .getProductsByType(productType, page, size)
      .pipe(
        catchError((error) => {
          console.error('Erreur lors de la récupération des produits :', error);
          console.error(
            'Échec du chargement des produits. Veuillez réessayer plus tard.'
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
