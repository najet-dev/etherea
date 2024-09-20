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
  addToCart(cart: Cart): Observable<any> {
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

    if (cart.product?.type === ProductType.HAIR && cart.selectedVolume?.id) {
      params = params.append('volumeId', cart.selectedVolume.id.toString());
    }

    return this.httpClient
      .post(`${this.apiUrl}/cart/addToCart`, null, { params })
      .pipe(
        tap(() => this.cartUpdated.emit()),
        catchError((error) => {
          console.error('Error adding item to cart:', error);
          return throwError(() => new Error('Failed to add item to cart.'));
        })
      );
  }

  // Mise à jour de la quantité d'un article du panier
  updateCartItem(
    userId: number,
    productId: number,
    newQuantity: number,
    volumeId?: number
  ): Observable<Cart> {
    const params = new HttpParams().set('quantity', newQuantity.toString());
    if (volumeId) {
      params.set('volumeId', volumeId.toString());
    }

    return this.httpClient
      .put<Cart>(`${this.apiUrl}/cart/${userId}/products/${productId}`, null, {
        params,
      })
      .pipe(
        tap(() => this.cartUpdated.emit()),
        catchError((error) => {
          console.error('Error updating cart item quantity:', error);
          return throwError(
            () => new Error('Failed to update cart item quantity.')
          );
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
