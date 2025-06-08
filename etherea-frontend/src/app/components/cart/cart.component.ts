import { Component, OnInit, inject } from '@angular/core';
import { Cart } from 'src/app/components/models/cart.model';
import { AuthService } from 'src/app/services/auth.service';
import { DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AppFacade } from 'src/app/services/appFacade.service';
import { Product } from '../models/product.model';
import { ProductVolume } from '../models/productVolume.model';
import { catchError, forkJoin, of, tap } from 'rxjs';
import { ProductTypeService } from 'src/app/services/product-type.service';
import { HairProduct } from '../models/hairProduct.model';
import { CartCalculationService } from 'src/app/services/cart-calculation.service';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
})
export class CartComponent implements OnInit {
  cartItems: Cart[] = [];
  cartTotal: number = 0;
  userId!: number;
  isCartEmpty: boolean = true;
  showConfirmDelete: boolean = false;
  itemIdToDelete!: number;
  showModal = false;
  private destroyRef = inject(DestroyRef);

  constructor(
    private authService: AuthService,
    private appFacade: AppFacade,
    public productTypeService: ProductTypeService,
    private cartCalculationService: CartCalculationService
  ) {
    this.appFacade.cartService.cartUpdated.subscribe(() => {
      this.loadCartItems();
    });
  }

  ngOnInit(): void {
    this.authService
      .getCurrentUser()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((user) => {
        console.log('Objet utilisateur reçu:', user);
        if (user && user.id) {
          this.userId = user.id;
          this.loadCartItems();
        } else {
          console.error("L'ID utilisateur n'est pas défini.");
        }
      });
  }

  loadCartItems() {
    if (!this.userId) {
      console.error(
        "L'ID utilisateur n'est pas défini. Impossible de charger les éléments du panier."
      );
      return;
    }

    this.appFacade.getCartItems(this.userId).subscribe({
      next: (cartItems) => {
        // Vérifier que les éléments du panier sont reçus
        if (cartItems && cartItems.length > 0) {
          this.cartItems = cartItems;
          this.isCartEmpty = this.cartItems.length === 0;
          this.loadProductDetails();
        } else {
          this.cartItems = [];
          this.isCartEmpty = true;
        }
      },
      error: (error) => {
        console.log(
          'Erreur lors de la récupération des éléments du panier :',
          error
        );
        this.cartItems = [];
        this.isCartEmpty = true;
      },
    });
  }

  loadProductDetails() {
    const productObservables = this.cartItems.map((item) =>
      this.appFacade.getProductById(item.productId).pipe(
        tap((product: Product | null) => {
          if (product) {
            item.product = product;
            this.initializeSelectedVolume(item);
          } else {
            console.error("Produit non trouvé pour l'id :", item.productId);
          }
        }),
        catchError((error) => {
          console.log('Erreur lors de la récupération du produit :', error);
          return of(null);
        })
      )
    );

    forkJoin(productObservables).subscribe(() => {
      this.calculateCartTotal();
    });
  }

  initializeSelectedVolume(item: Cart): void {
    // Initialiser selectedVolume si ce n'est pas défini
    if (!item.selectedVolume && item.volume) {
      item.selectedVolume = { ...item.volume };
    }

    // Vérifiez si le produit a des volumes avant de les utiliser
    if (
      this.productTypeService.isHairProduct(item.product) &&
      item.selectedVolume
    ) {
      const hairProduct = item.product as HairProduct; // Type assertion
      const selectedVol = hairProduct.volumes.find(
        (vol: ProductVolume) => vol.id === item.selectedVolume?.id
      );
      item.selectedVolume = selectedVol || item.selectedVolume;
    } else if (this.productTypeService.isFaceProduct(item.product)) {
      console.warn("Les produits faciaux n'ont pas de volumes :", item);
    } else {
      console.warn("Produit non reconnu pour l'article :", item);
    }
  }

  calculateCartTotal(): void {
    this.cartTotal = this.cartCalculationService.calculateCartTotal(
      this.cartItems
    );
  }

  incrementQuantity(item: Cart): void {
    item.quantity++;
    this.updateCartItem(item);
    this.calculateCartTotal();
  }

  decrementQuantity(item: Cart): void {
    if (item.quantity > 1) {
      item.quantity--;
      this.updateCartItem(item);
      this.calculateCartTotal();
    }
  }

  updateCartItem(item: Cart): void {
    if (item && item.userId && item.productId && item.quantity) {
      const volumeId = item.selectedVolume?.id;
      this.appFacade
        .updateCartItem(this.userId, item.productId, item.quantity, volumeId)
        .subscribe({
          next: (updatedItem) => {
            const index = this.cartItems.findIndex(
              (cartItem) => cartItem.productId === updatedItem.productId
            );
            if (index !== -1) {
              this.cartItems[index] = updatedItem;
              this.calculateCartTotal();
            }
          },
          error: (error) => {
            console.error(
              "Erreur lors de la mise à jour de l'article du panier :",
              error
            );
          },
        });
    } else {
      console.error("Données de l'article non valides :", item);
    }
  }

  confirmDeleteItem(cartItemId: number): void {
    this.itemIdToDelete = cartItemId;
    this.showConfirmDelete = true;
  }

  deleteItem(): void {
    this.appFacade.deleteCartItem(this.itemIdToDelete).subscribe({
      next: () => {
        this.showConfirmDelete = false;
        this.loadCartItems(); // Recharger le panier après suppression
      },
      error: (error) => {
        console.error('Failed to delete product from cart:', error);
        this.showConfirmDelete = false;
      },
    });
  }

  cancelDelete(): void {
    this.showConfirmDelete = false;
  }

  hideModal(): void {
    this.showModal = false;
  }
}
