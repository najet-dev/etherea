import { EventEmitter, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Cart } from '../components/models/cart.model';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private apiUrl = environment.apiUrl;
  private cartItemsSubject: BehaviorSubject<Cart[]> = new BehaviorSubject<
    Cart[]
  >([]);
  public carts$ = this.cartItemsSubject.asObservable(); // Observable pour les composants abonnés

  cartUpdated: EventEmitter<void> = new EventEmitter<void>();

  constructor(private httpClient: HttpClient) {}

  // Récupérer les articles du panier d'un utilisateur et mettre à jour le BehaviorSubject
  getCartItems(userId: number): Observable<Cart[]> {
    return this.httpClient.get<Cart[]>(`${this.apiUrl}/cart/${userId}`).pipe(
      tap((cartItems: Cart[]) => {
        this.cartItemsSubject.next(cartItems); // Met à jour le BehaviorSubject avec les nouveaux éléments du panier
      }),
      catchError((error) => {
        console.error('Error fetching cart items:', error);
        return throwError(() => error);
      })
    );
  }

  // Ajouter un article au panier
  addToCart(cart: Cart): Observable<Cart> {
    let params = new HttpParams()
      .set('userId', cart.userId.toString())
      .set('productId', cart.productId.toString())
      .set('quantity', cart.quantity.toString());

    if (cart.product.type === 'HAIR' && cart.selectedVolume) {
      params = params.set('volumeId', cart.selectedVolume.id.toString());
    }

    return this.httpClient
      .post<Cart>(`${this.apiUrl}/cart/addToCart`, null, { params })
      .pipe(
        tap((newCartItem: Cart) => {
          this.refreshCart(cart.userId); // Actualise le panier après ajout
        }),
        catchError((error) => {
          console.error('Error adding product to cart:', error);
          return throwError(() => error);
        })
      );
  }

  // Mettre à jour un article du panier
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
      tap(() => {
        this.refreshCart(userId); // Actualise le panier après mise à jour
      }),
      catchError((error) => {
        console.error('Error updating cart item:', error);
        const errorMessage = volumeId
          ? 'Failed to update cart item quantity for HAIR product.'
          : 'Failed to update cart item quantity for FACE product.';
        return throwError(() => new Error(errorMessage));
      })
    );
  }

  // Supprimer un article du panier
  deleteCartItem(cartItemId: number): Observable<void> {
    return this.httpClient
      .delete<void>(`${this.apiUrl}/cart/${cartItemId}`)
      .pipe(
        tap(() => {
          this.refreshCart(cartItemId); // Actualise le panier après suppression
        }),
        catchError((error) => {
          console.error('Error deleting cart item:', error);
          return throwError(() => error);
        })
      );
  }

  // Méthode pour rafraîchir les articles du panier après chaque modification
  private refreshCart(userId: number): void {
    this.getCartItems(userId).subscribe(); // Récupère les nouveaux éléments du panier et met à jour le BehaviorSubject
  }
}
