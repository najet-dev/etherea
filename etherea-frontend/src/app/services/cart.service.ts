import { EventEmitter, Injectable } from '@angular/core';
import {
  HttpClient,
  HttpErrorResponse,
  HttpParams,
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Cart } from '../components/models/cart.model';
import { IProduct, ProductType } from '../components/models/i-product';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private apiUrl = environment.apiUrl;
  cartUpdated: EventEmitter<void> = new EventEmitter<void>();

  constructor(private httpClient: HttpClient) {}

  // Récupère les éléments du panier d'un utilisateur
  getCartItems(userId: number): Observable<Cart[]> {
    return this.httpClient.get<Cart[]>(`${this.apiUrl}/cart/${userId}`).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          return throwError(() => new Error('User or cart not found.'));
        }
        return throwError(() => new Error('Failed to load cart items.'));
      })
    );
  }

  // Ajoute un élément au panier
  addToCart(cart: Cart): Observable<Cart> {
    console.log('Envoi des données du panier:', cart);

    if (!cart.productId || !cart.userId || cart.quantity <= 0) {
      console.error(
        'Données manquantes ou quantité invalide pour ajouter au panier:',
        cart
      );
      return throwError(
        () =>
          new Error('Produit, utilisateur non défini, ou quantité invalide.')
      );
    }

    let params = new HttpParams()
      .set('userId', cart.userId.toString())
      .set('productId', cart.productId.toString())
      .set('quantity', cart.quantity.toString());

    // Vérification pour les produits de type HAIR
    if (cart.product?.type === ProductType.HAIR) {
      if (cart.selectedVolume?.id) {
        console.log("Volume sélectionné lors de l'ajout:", cart.selectedVolume); // Ajoutez ce log
        params = params.append('volumeId', cart.selectedVolume.id.toString());
      } else {
        return throwError(
          () =>
            new Error('Volume ID est requis pour les produits de type HAIR.')
        );
      }
    }

    // Vérification pour les produits de type FACE
    if (cart.product?.type === ProductType.FACE && cart.selectedVolume) {
      return throwError(
        () =>
          new Error('Les produits de type FACE ne doivent pas avoir de volume.')
      );
    }

    return this.httpClient
      .post<Cart>(`${this.apiUrl}/cart/addToCart`, null, { params })
      .pipe(
        tap(() => this.cartUpdated.emit()),
        catchError((error) => {
          console.error('Error adding item to cart:', error);
          return throwError(() => new Error('Failed to add item to cart.'));
        })
      );
  }

  // Mise à jour de la quantité d'un article du panier avec gestion des produits de type FACE et HAIR
  updateCartItem(
    userId: number,
    productId: number,
    newQuantity: number,
    volumeId?: number
  ): Observable<Cart> {
    if (newQuantity <= 0) {
      return throwError(() => new Error('Quantity must be greater than 0.'));
    }

    const url = volumeId
      ? `${this.apiUrl}/cart/${userId}/products/${productId}/volume/${volumeId}`
      : `${this.apiUrl}/cart/${userId}/products/${productId}`;

    const params = new HttpParams().set('quantity', newQuantity.toString());

    return this.httpClient.put<Cart>(url, null, { params }).pipe(
      tap(() => this.cartUpdated.emit()),
      catchError((error) => {
        console.error('Error updating cart item:', error);
        const errorMessage = volumeId
          ? 'Failed to update cart item quantity for HAIR product.'
          : 'Failed to update cart item quantity for FACE product.';
        return throwError(() => new Error(errorMessage));
      })
    );
  }

  // Supprime un élément du panier
  deleteCartItem(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.apiUrl}/cart/${id}`).pipe(
      tap(() => this.cartUpdated.emit()), // Emit update after deletion
      catchError((error) => {
        console.error('Error deleting cart item:', error);
        return throwError(() => new Error('Failed to delete cart item.'));
      })
    );
  }
}
