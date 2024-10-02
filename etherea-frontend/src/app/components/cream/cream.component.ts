import { Component, OnDestroy, OnInit } from '@angular/core';
import { Product } from '../models/Product.model';
import { Observable, of } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';
import { ProductService } from 'src/app/services/product.service';
import { FavoriteService } from 'src/app/services/favorite.service';
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';
import { DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AppFacade } from 'src/app/services/appFacade.service';
import { ProductVolume } from '../models/ProductVolume.model';
import { ProductTypeService } from 'src/app/services/product-type.service'; // Ajout du service
import { FaceProduct } from '../models'; // Assurez-vous que ce modèle est correctement importé
import { IProductVolume } from '../models/IProductVolume.model';

@Component({
  selector: 'app-day-cream',
  templateUrl: './cream.component.html',
  styleUrls: ['./cream.component.css'],
})
export class CreamComponent implements OnInit {
  products$: Observable<Product[]> = new Observable<Product[]>();
  userId: number | null = null;
  private destroyRef = inject(DestroyRef);

  constructor(
    private productService: ProductService,
    private authService: AuthService,
    private favoriteService: FavoriteService,
    private appFacade: AppFacade,
    private router: Router,
    public productTypeService: ProductTypeService
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
    const productType = 'FACE'; // Type de produit pour le visage
    const page = 0; // Numéro de la page
    const size = 10; // Taille de la page

    this.products$ = this.appFacade
      .getProductsByType(productType, page, size)
      .pipe(
        switchMap((products: Product[]) => {
          // Filtrer pour ne garder que les FaceProducts
          const faceProducts = products.filter((product) =>
            this.productTypeService.isFaceProduct(product)
          );

          // Si l'utilisateur est connecté, appliquez le service de favoris
          if (this.userId !== null) {
            return this.appFacade.productsFavorites(faceProducts);
          }
          return of(faceProducts); // Retourne uniquement les FaceProducts
        }),
        catchError((error) => {
          console.error('Error fetching products:', error);
          console.error('Failed to load products. Please try again later.');
          return of([] as FaceProduct[]); // Cast to FaceProduct[]
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
