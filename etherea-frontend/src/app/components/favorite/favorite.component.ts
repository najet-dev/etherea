import { Component, OnInit, inject, DestroyRef } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';
import { Favorite } from '../models/favorite.model';
import { Product } from '../models/product.model';
import { ProductVolume } from '../models/productVolume.model';
import { Router } from '@angular/router';
import { AppFacade } from 'src/app/services/appFacade.service';
import { ProductTypeService } from 'src/app/services/product-type.service';
import { HairProduct } from '../models/hairProduct.model';
import { FaceProduct } from '../models/faceProduct.model';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Cart } from '../models/cart.model';
import { forkJoin, map, switchMap } from 'rxjs';

@Component({
  selector: 'app-favorite',
  templateUrl: './favorite.component.html',
  styleUrls: ['./favorite.component.css'],
})
export class FavoriteComponent implements OnInit {
  favorites: Favorite[] = [];
  selectedVolumes: { [productId: number]: ProductVolume } = {};
  userId!: number;
  showSuccessMessage = false; // Pour contrôler l'affichage du message de succès
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
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((user) => {
        if (user && user.id) {
          this.userId = user.id;
          this.loadFavorites();
        }
      });
  }

  loadFavorites(): void {
    this.appFacade
      .getUserFavorites(this.userId)
      .pipe(
        switchMap((favorites) => {
          const productObservables = favorites.map((favorite) =>
            this.appFacade.getProductById(favorite.productId)
          );
          return forkJoin(productObservables).pipe(
            map((products) => {
              favorites.forEach((favorite, index) => {
                favorite.product = products[index];

                if (this.productTypeService.isHairProduct(favorite.product)) {
                  const hairProduct = favorite.product as HairProduct;

                  // Initialiser le volume sélectionné pour chaque produit
                  if (hairProduct.volumes && hairProduct.volumes.length > 0) {
                    this.selectedVolumes[favorite.productId] =
                      hairProduct.volumes[0];
                  }
                }
              });
              return favorites;
            })
          );
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (favorites) => {
          this.favorites = favorites;
        },
        error: (error) => {
          console.error('Error loading favorites:', error);
        },
      });
  }

  // Méthode pour supprimer le produit des favoris
  removeFavorite(productId: number): void {
    this.appFacade.removeFavorite(this.userId, productId).subscribe({
      next: () => {
        // Mettre à jour la liste des favoris après suppression
        this.favorites = this.favorites.filter(
          (favorite) => favorite.productId !== productId
        );

        // Afficher le message de succès pendant 3 secondes
        this.showSuccessMessage = true;
        setTimeout(() => {
          this.showSuccessMessage = false;
        }, 3000);
      },
      error: (error) => {
        console.error('Error removing favorite:', error);
      },
    });
  }

  openProductPopup(
    product: Product,
    selectedVolume: ProductVolume | undefined
  ): void {
    const cartItem: Cart = {
      id: 0,
      userId: this.userId,
      productId: product.id,
      quantity: 1,
      product: product,
      selectedVolume: this.productTypeService.isHairProduct(product)
        ? selectedVolume
        : undefined,
      hairProduct: this.productTypeService.isHairProduct(product)
        ? (product as HairProduct)
        : null,
      faceProduct: this.productTypeService.isFaceProduct(product)
        ? (product as FaceProduct)
        : null,
    };

    const subTotal = this.productTypeService.isFaceProduct(product)
      ? (product as FaceProduct).basePrice * cartItem.quantity
      : (selectedVolume?.price || 0) * cartItem.quantity;

    this.appFacade.cartService.addToCart(cartItem).subscribe({
      next: () => {
        this.router.navigateByUrl('/cart');
      },
      error: (error) => {
        console.error('Error adding product to cart:', error);
      },
    });
  }
}
