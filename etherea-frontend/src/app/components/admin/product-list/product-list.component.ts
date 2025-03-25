import { Component, DestroyRef, inject } from '@angular/core';
import { Product } from '../../models';
import { catchError, Observable, of, switchMap, tap } from 'rxjs';
import { AuthService } from 'src/app/services/auth.service';
import { AppFacade } from 'src/app/services/appFacade.service';
import { ProductTypeService } from 'src/app/services/product-type.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ProductService } from 'src/app/services/product.service';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css'],
})
export class ProductListComponent {
  products$: Observable<Product[]> = new Observable<Product[]>();
  userId: number | null = null;
  private destroyRef = inject(DestroyRef);
  showSuccessMessage = false;
  constructor(
    private authService: AuthService,
    private appFacade: AppFacade,
    public productTypeService: ProductTypeService,
    private productService: ProductService
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
    this.products$ = this.appFacade.getProducts().pipe(
      catchError((error) => {
        console.error('Error fetching products:', error);
        return of([]);
      }),
      takeUntilDestroyed(this.destroyRef)
    );
  }

  deleteProduct(productId: number) {
    this.productService
      .deleteProduct(productId)
      .pipe(
        tap(() => {
          this.showSuccessMessage = true;

          // Masquer le message après 3 secondes
          setTimeout(() => {
            this.showSuccessMessage = false;
          }, 3000);
        }),
        switchMap(() => this.appFacade.getProducts()), // Recharger la liste des produits après suppression
        catchError((error) => {
          console.error('Erreur lors de la suppression du produit:', error);
          return of([]); // Retourner une liste vide en cas d'erreur pour éviter le plantage
        })
      )
      .subscribe((products) => {
        this.products$ = of(products); // Mettre à jour la liste des produits affichée
      });
  }
}
