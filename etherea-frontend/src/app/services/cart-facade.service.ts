// cart-facade.service.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { Cart } from '../components/models/cart.model';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { catchError, map } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root',
})
export class CartServiceFacade {
  private apiUrl = environment.apiUrl;
  private cartUpdatedSubject: BehaviorSubject<void> = new BehaviorSubject<void>(
    undefined
  );

  constructor(
    private httpClient: HttpClient,
    private storageService: StorageService
  ) {}

  addToCart(cart: Cart): Observable<Cart> {
    return this.httpClient
      .post<Cart>(`${this.apiUrl}/cart/addToCart`, cart)
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
          return throwError(() => error);
        })
      );
  }

  deleteCartItem(id: number): Observable<Cart> {
    return this.httpClient.delete<Cart>(`${this.apiUrl}/cart/${id}`).pipe(
      catchError((error) => {
        console.error('Error deleting cart item:', error);
        return throwError(() => error);
      })
    );
  }
  // Méthode pour notifier les mises à jour du panier
  cartUpdated(): Observable<void> {
    return this.cartUpdatedSubject.asObservable();
  }
}
