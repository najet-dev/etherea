import { Component, OnInit, inject, DestroyRef } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';
import { Favorite } from '../models/favorite.model';
import { Product } from '../models/Product.model';
import { ProductVolume } from '../models/ProductVolume.model';
import { ProductService } from 'src/app/services/product.service';
import { IProduct, ProductType } from '../models/i-product.model';
import { forkJoin } from 'rxjs';
import { switchMap, map } from 'rxjs/operators';
import { Cart } from '../models/cart.model';
import { CartService } from 'src/app/services/cart.service';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AppFacade } from 'src/app/services/appFacade.service';
import { ProductTypeService } from 'src/app/services/product-type.service';
import { HairProduct } from '../models/HairProduct.model';
import { FaceProduct } from '../models/FaceProduct.model';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ProductSummaryComponent } from '../product-summary/product-summary.component';
import { Cart } from '../models/cart.model';
import { forkJoin, map, switchMap } from 'rxjs';

@Component({
  selector: 'app-favorite',
  templateUrl: './favorite.component.html',
  styleUrls: ['./favorite.component.css'],
})
export class FavoriteComponent implements OnInit {
  favorites: Favorite[] = [];
  userId!: number;
  selectedVolume: ProductVolume | undefined;
  showModal = false; // Pour contrôler l'affichage de la modale
  confirmedProductId!: number; // ID du produit à supprimer après confirmation
  product: IProduct | null = null;
  selectedVolume: IProductVolume | null = null;
  showModal = false;
  confirmedProductId!: number;
  private destroyRef = inject(DestroyRef);
  ProductType = ProductType;

  constructor(
    private authService: AuthService,
    private appFacade: AppFacade,
    private dialog: MatDialog,
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

                  if (hairProduct.volumes && hairProduct.volumes.length > 0) {
                    this.selectedVolume = hairProduct.volumes[0];
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

  // Afficher la modale de confirmation
  confirmRemoveFavorite(productId: number): void {
    this.confirmedProductId = productId; // Stocker l'ID du produit à supprimer
    this.showModal = true; // Afficher la modale
  }

  // Méthode pour cacher la modale
  hideModal(): void {
    this.showModal = false;
  }

  // Méthode pour supprimer le produit des favoris
  removeFavorite(productId: number): void {
    this.appFacade.removeFavorite(this.userId, productId).subscribe({
      next: () => {
        // Mettre à jour la liste des favoris après suppression
      next: (response) => {
        this.favorites = this.favorites.filter(
          (favorite) => favorite.productId !== productId
        );
        this.hideModal(); // Masquer la modale après suppression
      },
      error: (error) => {
        console.error('Error removing favorite:', error);
      },
    });
  }

  hideModal(): void {
    this.showModal = false;
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
        const dialogRef = this.dialog.open(ProductSummaryComponent, {
          width: '60%',
          height: '80%',
          data: {
            product: product,
            quantity: cartItem.quantity,
            selectedVolume: this.productTypeService.isHairProduct(product)
              ? cartItem.selectedVolume
              : null,
            cart: cartItem,
            subTotal: subTotal,
          },
        });

        dialogRef.afterClosed().subscribe({
          next: (result) => {
            if (result === 'goToCart') {
              this.router.navigateByUrl('/cart');
            }
          },
        });
      },
      error: (error) => {
        console.error('Error adding product to cart:', error);
      },
    });
  }
}
