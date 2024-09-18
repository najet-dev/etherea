import { EventEmitter, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Cart } from '../components/models/cart.model';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private apiUrl = environment.apiUrl;
  cartUpdated: EventEmitter<void> = new EventEmitter<void>();

  constructor(private httpClient: HttpClient) {}

  getCartItems(userId: number): Observable<Cart[]> {
    return this.httpClient.get<Cart[]>(`${this.apiUrl}/cart/${userId}`).pipe(
      catchError((error) => {
        console.error('Error fetching cart items:', error);
        return throwError(() => new Error('Failed to fetch cart items.'));
      })
    );
  }

  addToCart(cart: Cart): Observable<any> {
    if (!cart.selectedVolume?.id) {
      console.error('Volume ID is undefined');
      return throwError(() => new Error('Volume ID is undefined'));
    }

    const params = new HttpParams()
      .set('userId', cart.userId.toString())
      .set('productId', cart.productId.toString())
      .set('volumeId', cart.selectedVolume.id.toString())
      .set('quantity', cart.quantity.toString());

    return this.httpClient.post(`${this.apiUrl}/cart/addToCart`, {
      userId: cart.userId,
      productId: cart.productId,
      volumeId: cart.selectedVolume.id,
      quantity: cart.quantity,
    });
  }

  updateCartItem(
    userId: number,
    productId: number,
    newQuantity: number
  ): Observable<Cart> {
    const params = new HttpParams().set('newQuantity', newQuantity.toString());

    return this.httpClient
      .put<Cart>(`${this.apiUrl}/cart/${userId}/products/${productId}`, null, {
        params,
      })
      .pipe(
        catchError((error) => {
          console.error('Error updating cart item quantity:', error);
          return throwError(
            () => new Error('Failed to update cart item quantity.')
          );
        }),
        tap(() => this.cartUpdated.emit())
      );
  }

  deleteCartItem(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.apiUrl}/cart/${id}`).pipe(
      catchError((error) => {
        console.error('Error deleting cart item:', error);
        return throwError(() => new Error('Failed to delete cart item.'));
      }),
      tap(() => this.cartUpdated.emit())
    );
  }
}
