import { EventEmitter, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Cart } from '../components/models/cart.model';
import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private apiUrl = environment.apiUrl;
  private cartKey = 'cartItems';
  cartUpdated: EventEmitter<void> = new EventEmitter<void>(); // Événement pour indiquer la mise à jour du panier

  constructor(
    private httpClient: HttpClient,
    private storageService: StorageService
  ) {}

  // Méthodes pour gérer le panier côté frontend
  getLocalCartItems(): Cart[] {
    return this.storageService.get(this.cartKey) || [];
  }

  addToLocalCart(cart: Cart): void {
    let cartItems = this.getLocalCartItems();
    cartItems.push(cart);
    this.storageService.set(this.cartKey, cartItems);
  }

  clearLocalCart(): void {
    this.storageService.remove(this.cartKey);
  }

  // Méthodes pour interagir avec le backend
  getCartItems(userId: number): Observable<Cart[]> {
    return this.httpClient.get<Cart[]>(`${this.apiUrl}/cart/${userId}`).pipe(
      catchError((error) => {
        console.error('Error fetching cart items:', error);
        return throwError(error);
      })
    );
  }

  addToCart(cart: Cart): Observable<Cart> {
    const params = new HttpParams()
      .set('userId', cart.userId.toString())
      .set('productId', cart.productId.toString())
      .set('quantity', cart.quantity.toString());

    return this.httpClient
      .post<Cart>(`${this.apiUrl}/cart/addToCart`, null, {
        params,
      })
      .pipe(
        catchError((error) => {
          console.error('Error adding product to cart:', error);
          return throwError(() => error); // Utilisation recommandée d'une fonction factory
        })
      );
  }
  updateToCart(
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
        }),
        tap(() => {
          // Émettre l'événement une fois que la mise à jour du panier est effectuée avec succès
          this.cartUpdated.emit();
        })
      );
  }
}
