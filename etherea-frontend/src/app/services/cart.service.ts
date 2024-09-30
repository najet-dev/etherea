import { EventEmitter, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Cart } from '../components/models/cart.model';
import { StorageService } from './storage.service';
import { ProductVolume } from '../components/models/ProductVolume.model';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private apiUrl = environment.apiUrl;
  cartUpdated: EventEmitter<void> = new EventEmitter<void>(); // Événement pour indiquer la mise à jour du panier

  constructor(private httpClient: HttpClient) {}

  getCartItems(userId: number): Observable<Cart[]> {
    console.log('Fetching cart items for user ID:', userId); // Debugging line
    return this.httpClient.get<Cart[]>(`${this.apiUrl}/cart/${userId}`).pipe(
      catchError((error) => {
        console.error('Error fetching cart items:', error);
        return throwError(() => error);
      })
    );
  }

  addToCart(cart: Cart): Observable<Cart> {
    // Initialisation des paramètres avec userId, productId et quantity
    let params = new HttpParams()
      .set('userId', cart.userId.toString())
      .set('productId', cart.productId.toString())
      .set('quantity', cart.quantity.toString());

    // Si le produit est de type HAIR, ajouter volumeId dans les paramètres
    if (cart.product.type === 'HAIR' && cart.selectedVolume) {
      params = params.set('volumeId', cart.selectedVolume.id.toString());
    }

    // Effectuer la requête POST avec les paramètres
    return this.httpClient
      .post<Cart>(`${this.apiUrl}/cart/addToCart`, null, { params })
      .pipe(
        catchError((error) => {
          console.error('Error adding product to cart:', error);
          return throwError(() => error);
        })
      );
  }

  updateCartItem(
    userId: number,
    productId: number,
    newQuantity: number,
    volumeId?: number
  ): Observable<Cart> {
    if (newQuantity <= 0) {
      return throwError(() => new Error('Quantity must be greater than 0.'));
    }

    // Vérifier si le produit est de type HAIR
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

  deleteCartItem(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.apiUrl}/cart/${id}`).pipe(
      catchError((error) => {
        console.error('Error deleting cart item:', error);
        return throwError(() => error);
      }),
      tap(() => {
        // Émettre l'événement une fois que la suppression du produit du panier est effectuée avec succès
        this.cartUpdated.emit();
      })
    );
  }
}
