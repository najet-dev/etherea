import { EventEmitter, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Cart } from '../components/models/cart.model';
import { StorageService } from './storage.service';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private apiUrl = environment.apiUrl;
  userId: number | null = null; // Initialize userId property
  private cartKey = 'cartItems';

  cartUpdated: EventEmitter<void> = new EventEmitter<void>(); // Événement pour indiquer la mise à jour du panier
  // Panier local
  private localCartSubject: BehaviorSubject<Cart[]> = new BehaviorSubject<
    Cart[]
  >([]);
  public localCart$: Observable<Cart[]> = this.localCartSubject.asObservable();

  constructor(
    private httpClient: HttpClient,
    private storageService: StorageService
  ) {}

  // Méthodes pour interagir avec le backend
  getCartItems(userId: number): Observable<Cart[]> {
    // Obtenir les éléments du panier du stockage local
    const localCartItems = this.storageService.loadLocalCart() || [];

    // Si l'utilisateur est connecté, obtenir les éléments du panier depuis le backend
    if (userId) {
      return this.httpClient.get<Cart[]>(`${this.apiUrl}/cart/${userId}`).pipe(
        catchError((error) => {
          console.error('Error fetching cart items:', error);
          return throwError(() => error);
        }),
        map((backendCartItems: Cart[]) => {
          // Fusionner les éléments du panier du backend avec ceux du stockage local
          return [...localCartItems, ...backendCartItems];
        })
      );
    } else {
      // Si l'utilisateur n'est pas connecté, renvoyer simplement les éléments du panier du stockage local
      return of(localCartItems);
    }
  }

  addToCart(cart: Cart): Observable<Cart> {
    if (this.userId !== null) {
      const params = new HttpParams()
        .set('userId', this.userId.toString())
        .set('productId', cart.productId.toString())
        .set('quantity', cart.quantity.toString());

      return this.httpClient
        .post<Cart>(`${this.apiUrl}/cart/addToCart`, null, { params })
        .pipe(
          catchError((error) => {
            console.error('Error adding product to cart:', error);
            return throwError(() => error);
          })
        );
    } else {
      let localCart = this.loadLocalCart();
      localCart.push(cart);
      this.saveLocalCart(localCart);
      // Emit an event to indicate that the cart has been updated
      this.cartUpdated.next();
      return new Observable<Cart>((observer) => {
        observer.next(cart);
        observer.complete();
      });
    }
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
        }),
        tap(() => {
          // Émettre l'événement une fois que la mise à jour du panier est effectuée avec succès
          this.cartUpdated.emit();
        })
      );
  }

  // Delete cart item
  deleteCartItem(id: number): Observable<Cart | null> {
    // Modifier le type de retour pour inclure null
    if (this.userId !== null) {
      return this.httpClient.delete<Cart>(`${this.apiUrl}/cart/${id}`).pipe(
        catchError((error) => {
          console.error('Error deleting cart item:', error);
          return throwError(() => error);
        })
      );
    } else {
      let localCart = this.loadLocalCart();
      const index = localCart.findIndex((item) => item.id === id);
      if (index !== -1) {
        localCart.splice(index, 1);
        this.saveLocalCart(localCart);
        // Emit an event to indicate that the cart has been updated
        this.cartUpdated.next();
      }
      return of(null); // Retourner un observable de type Cart | null
    }
  }

  getLocalCart(): Cart[] | null {
    return this.storageService.getItem(this.cartKey) as Cart[] | null;
  }

  // Save local cart items
  saveLocalCart(cart: Cart[] | null): void {
    const cartJson = JSON.stringify(cart);
    this.storageService.setItem(this.cartKey, cartJson);
  }

  // Load local cart items
  loadLocalCart(): Cart[] {
    const cartJson = this.storageService.getItem(this.cartKey);
    const cart: Cart[] = cartJson ? JSON.parse(cartJson) : [];
    return cart;
  }
  // Lorsque l'utilisateur se connecte

  // Synchronisation du panier local avec le panier du serveur
  private syncCartWithServer(
    localCart: Cart[],
    userId: number
  ): Observable<void> {
    return this.httpClient
      .put<void>(`${this.apiUrl}/cart/${userId}`, localCart)
      .pipe(
        catchError((error) => {
          console.error('Error syncing cart with server:', error);
          throw error;
        }),
        tap(() => {
          console.log('Cart synchronized with server successfully');
        })
      );
  }

  // Méthode pour vider le panier local
  private clearLocalCart(): void {
    this.storageService.clean();
    this.localCartSubject.next([]);
  }
}
