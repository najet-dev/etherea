import { Component, OnInit, inject } from '@angular/core';
import { CartService } from 'src/app/services/cart.service';
import { Cart } from 'src/app/components/models/cart.model';
import { AuthService } from 'src/app/services/auth.service';
import { DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AppFacade } from 'src/app/services/appFacade.service';
import { ProductType } from '../models/i-product.model';
import { catchError, forkJoin, of, tap } from 'rxjs';

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

  constructor(private authService: AuthService, private appFacade: AppFacade) {
    this.appFacade.cartService.cartUpdated.subscribe(() => {
      this.loadCartItems();
    });
  }

  ngOnInit(): void {
    this.authService
      .getCurrentUser()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((user) => {
        if (user && user.id) {
          this.userId = user.id;
          this.loadCartItems();
        } else {
          console.error("L'ID utilisateur n'est pas défini.");
        }
      });
  }

  loadCartItems() {
    // Vérifier si userId est défini
    if (!this.userId) {
      console.error(
        "L'ID utilisateur n'est pas défini. Impossible de charger les éléments du panier."
      );
      return;
    }

    this.appFacade.getCartItems(this.userId).subscribe({
      next: (cartItems) => {
        this.cartItems = cartItems;
        this.isCartEmpty = this.cartItems.length === 0;

        const productObservables = this.cartItems.map((item) =>
          this.appFacade.getProductById(item.productId).pipe(
            tap((product) => {
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
      },
      error: (error) => {
        console.log(
          'Erreur lors de la récupération des éléments du panier :',
          error
        );
      },
    });
  }

  initializeSelectedVolume(item: Cart): void {
    // Initialiser selectedVolume si ce n'est pas défini
    if (!item.selectedVolume && item.volume) {
      item.selectedVolume = { ...item.volume };
    }

    if (item.product.volumes && item.selectedVolume) {
      const selectedVol = item.product.volumes.find(
        (vol) => vol.id === item.selectedVolume?.id
      );
      item.selectedVolume = selectedVol || item.selectedVolume;
    } else {
      console.warn("selectedVolume est indéfini pour l'article :", item);
    }
  }

  calculateCartTotal(): void {
    this.cartTotal = this.cartItems.reduce((total, item) => {
      if (item.product) {
        if (item.product.type === ProductType.HAIR && item.selectedVolume) {
          item.subTotal = item.selectedVolume.price * item.quantity;
        } else if (
          item.product.type === ProductType.FACE &&
          item.product.basePrice !== undefined
        ) {
          item.subTotal = item.product.basePrice * item.quantity;
        }

        return total + (item.subTotal || 0);
      }
      return total;
    }, 0);
  }

  incrementQuantity(item: Cart): void {
    item.quantity++;
    this.updateCartItem(item);
  }

  decrementQuantity(item: Cart): void {
    if (item.quantity > 1) {
      item.quantity--;
      this.updateCartItem(item);
    }
  }

  updateCartItem(item: Cart): void {
    if (item && item.userId && item.productId && item.quantity) {
      const volumeId = item.selectedVolume?.id;
      this.appFacade
        .updateCartItem(this.userId, item.productId, item.quantity, volumeId)
        .subscribe({
          next: (updatedItem) => {
            console.log('Cart item updated successfully');
            const index = this.cartItems.findIndex(
              (cartItem) => cartItem.productId === updatedItem.productId
            );
            if (index !== -1) {
              this.cartItems[index] = updatedItem;
              this.calculateCartTotal();
            }
          },
          error: (error) => {
            console.error('Error updating cart item:', error);
          },
        });
    } else {
      console.error('Invalid item data:', item);
    }
  }

  confirmDeleteItem(id: number): void {
    this.itemIdToDelete = id;
    this.showConfirmDelete = true;
  }

  deleteItem(): void {
    this.appFacade.cartService.deleteCartItem(this.itemIdToDelete).subscribe({
      next: () => {
        console.log('Product deleted from cart successfully');
        this.showConfirmDelete = false;
        this.loadCartItems();
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
