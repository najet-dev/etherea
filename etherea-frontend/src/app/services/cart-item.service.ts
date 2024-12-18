import { Injectable } from '@angular/core';
import { Observable, forkJoin, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { ProductTypeService } from './product-type.service';
import { AppFacade } from './appFacade.service';
import { Cart } from '../components/models/cart.model';
import { CartCalculationService } from './cart-calculation.service';

@Injectable({
  providedIn: 'root',
})
export class CartItemService {
  cartItems: Cart[] = []; // Stocke les articles du panier
  cartTotal: number = 0; // Stocke le total du panier

  constructor(
    private appFacade: AppFacade,
    private productTypeService: ProductTypeService,
    private cartCalculationService: CartCalculationService
  ) {}

  /**
   * Charge les articles du panier pour un utilisateur donné.
   * @param userId - ID de l'utilisateur.
   * @returns Observable<Cart[]> - Les articles du panier.
   */
  loadCartItems(userId: number): Observable<Cart[]> {
    return this.appFacade.getCartItems(userId).pipe(
      tap((cartItems) => {
        console.log('Articles récupérés depuis l’API:', cartItems); // Debug: Vérifie les articles reçus
        this.cartItems = cartItems; // Met à jour le tableau `cartItems`

        // Pour chaque article, récupère les détails du produit
        const productObservables = cartItems.map((item) =>
          this.appFacade.getProductById(item.productId).pipe(
            tap((product) => {
              item.product = product; // Associe le produit à l'article
              if (product) this.initializeSelectedVolume(item); // Initialise les volumes si nécessaire
            }),
            catchError((error) =>
              this.handleError('Récupération produit', error)
            )
          )
        );

        // Une fois tous les produits récupérés, calcule le total
        forkJoin(productObservables).subscribe(() => this.calculateCartTotal());
      }),
      catchError((error) =>
        this.handleError('Chargement des articles du panier', error)
      )
    );
  }

  /**
   * Calcule le total du panier.
   */
  calculateCartTotal(): void {
    this.cartTotal = this.cartCalculationService.calculateCartTotal(
      this.cartItems
    );
  }

  /**
   * Initialise le volume sélectionné pour un article donné.
   * @param item - Article du panier.
   */
  private initializeSelectedVolume(item: Cart): void {
    if (!item.selectedVolume && item.volume) {
      item.selectedVolume = { ...item.volume };
    }

    if (
      this.productTypeService.isHairProduct(item.product) &&
      item.selectedVolume
    ) {
      const selectedVol = item.product.volumes?.find(
        (vol) => vol.id === item.selectedVolume?.id
      );
      item.selectedVolume = selectedVol || item.selectedVolume;
    }
  }

  /**
   * Gère les erreurs lors des appels API.
   * @param context - Contexte de l'erreur.
   * @param error - Erreur.
   * @returns Observable<[]> - Observable vide.
   */
  private handleError(context: string, error?: unknown): Observable<[]> {
    console.error(`Erreur lors de ${context}:`, error);
    return of([]);
  }
}
