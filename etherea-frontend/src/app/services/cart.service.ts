import { EventEmitter, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Cart } from '../components/models/cart.model';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private apiUrl = environment.apiUrl;
  private cartItemsSubject = new BehaviorSubject<Cart[]>([]);
  public carts$ = this.cartItemsSubject.asObservable();

  cartUpdated = new EventEmitter<void>();

  constructor(private httpClient: HttpClient) {}

  //cartItem
  getCartItems(userId: number): Observable<Cart[]> {
    console.log('Appel de getCartItems avec userId:', userId);
    return this.httpClient
      .get<Cart[]>(`${this.apiUrl}/cartItem/${userId}`)
      .pipe(
        tap((response) =>
          console.log('Réponse reçue de getCartItems:', response)
        ),
        catchError((error) => {
          console.error(
            'Erreur lors de la récupération des articles du panier :',
            error
          );
          return throwError(
            () => new Error('Impossible de récupérer les articles du panier.')
          );
        })
      );
  }

  addToCart(cart: Cart): Observable<Cart> {
    const body = {
      userId: cart.userId,
      productId: cart.productId,
      quantity: cart.quantity,
      volume:
        cart.product.type === 'HAIR' && cart.selectedVolume
          ? { id: cart.selectedVolume.id }
          : null,
    };

    console.log('Appel de addToCart avec body:', body);

    return this.httpClient
      .post<Cart>(`${this.apiUrl}/cartItem/addToCart`, body)
      .pipe(
        tap((response) => console.log('Article ajouté au panier:', response)),
        tap(() => this.refreshCart(cart.userId)),
        catchError((error) => {
          console.error('Erreur lors de l’ajout au panier :', error);
          return throwError(() => new Error('Échec de l’ajout au panier.'));
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
      console.warn(
        'Tentative de mise à jour avec une quantité invalide:',
        newQuantity
      );
      return throwError(
        () => new Error('La quantité doit être supérieure à 0.')
      );
    }

    const body = {
      userId: userId,
      productId: productId,
      quantity: newQuantity,
      ...(volumeId && { volume: { id: volumeId } }),
    };

    const endpoint =
      volumeId != null
        ? `${this.apiUrl}/cartItem/updateProductHair`
        : `${this.apiUrl}/cartItem/updateProductFace`;

    console.log(
      'Appel de updateCartItem avec endpoint:',
      endpoint,
      'et body:',
      body
    );

    return this.httpClient.put<Cart>(endpoint, body).pipe(
      tap((response) => console.log('Article mis à jour:', response)),
      tap(() => this.refreshCart(userId)),
      catchError((error) => {
        console.error('Erreur lors de la mise à jour du panier :', error);
        return throwError(
          () => new Error("Impossible de mettre à jour l'article du panier.")
        );
      })
    );
  }

  deleteCartItem(cartItemId: number): Observable<void> {
    return this.httpClient
      .delete<void>(`${this.apiUrl}/cartItem/${cartItemId}`)
      .pipe(
        tap(() => this.cartUpdated.emit()),
        catchError((error) => {
          console.error('Erreur lors de la suppression de l’article :', error);
          return throwError(
            () => new Error('Échec de la suppression de l’article.')
          );
        })
      );
  }

  public refreshCart(userId: number): void {
    this.getCartItems(userId).subscribe({
      error: (error) =>
        console.error('Erreur lors du rafraîchissement du panier :', error),
    });
  }
  //cart
  getCartId(userId: number): Observable<number> {
    return this.httpClient.get<number>(`${this.apiUrl}/cart/user/${userId}`);
  }
}
