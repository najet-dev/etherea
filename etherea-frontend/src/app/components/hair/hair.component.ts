import { Component, OnInit, OnDestroy } from '@angular/core';
import { Product } from '../models/Product.model';
import { Observable, of } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';
import { DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AppFacade } from 'src/app/services/appFacade.service';
import { ProductTypeService } from 'src/app/services/product-type.service';
import { HairProduct } from '../models/hairProduct.model';

@Component({
  selector: 'app-hair',
  templateUrl: './hair.component.html',
  styleUrls: ['./hair.component.css'],
})
export class HairComponent implements OnInit {
  products$: Observable<Product[]> = new Observable<Product[]>();
  userId: number | null = null;
  private destroyRef = inject(DestroyRef);

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
    const page = 0; // Numéro de la page
    const size = 10; // Taille de la page

    this.products$ = this.appFacade
      .getProductsByType(productType, page, size)
      .pipe(
        switchMap((products: Product[]) => {
          // Filtrer pour ne garder que les HairProducts
          const hairProducts = products.filter((product) =>
            this.productTypeService.isHairProduct(product)
          );

          // Si l'utilisateur est connecté, appliquez le service de favoris
          if (this.userId !== null) {
            return this.appFacade.productsFavorites(hairProducts);
          }
          return of(hairProducts); // Retourne uniquement les HairProduct
        }),
        catchError((error) => {
          console.error('Error fetching products:', error);
          console.error('Failed to load products. Please try again later.');
          return of([] as HairProduct[]); // Cast to HairProduct[]
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
