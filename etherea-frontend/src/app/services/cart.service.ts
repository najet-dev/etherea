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
    const localCartItems = this.loadLocalCart() || [];

    if (userId) {
      return this.httpClient.get<Cart[]>(`${this.apiUrl}/cart/${userId}`).pipe(
        catchError((error) => {
          console.error('Error fetching cart items:', error);
          return throwError(() => error);
        }),
        map((backendCartItems: Cart[]) => {
          // Fusion correcte des éléments du panier
          const mergedCart: Cart[] = [];

          // Créer une carte pour suivre les quantités
          const quantityMap = new Map<number, number>();

          // Ajouter les éléments du backend au panier fusionné
          backendCartItems.forEach((item) => {
            mergedCart.push(item);
            quantityMap.set(item.productId, item.quantity);
          });

          // Ajouter les éléments du panier local
          localCartItems.forEach((item) => {
            if (quantityMap.has(item.productId)) {
              // Si le produit existe déjà, mettre à jour la quantité
              const updatedQuantity =
                (quantityMap.get(item.productId) || 0) + item.quantity;
              const index = mergedCart.findIndex(
                (cartItem) => cartItem.productId === item.productId
              );
              if (index !== -1) {
                mergedCart[index].quantity = updatedQuantity;
              }
            } else {
              // Sinon, ajouter le produit
              mergedCart.push(item);
              quantityMap.set(item.productId, item.quantity);
            }
          });

          return mergedCart;
        })
      );
    } else {
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
      // Charger le panier local
      let localCart = this.loadLocalCart();

      // Vérifier si le produit existe déjà
      const existingProductIndex = localCart.findIndex(
        (item) => item.productId === cart.productId
      );

      if (existingProductIndex !== -1) {
        // Si le produit existe, mettre à jour la quantité
        localCart[existingProductIndex].quantity += cart.quantity;
      } else {
        // Sinon, ajouter le produit
        localCart.push(cart);
      }

      this.saveLocalCart(localCart);
      this.cartUpdated.next();

      return new Observable<Cart>((observer) => {
        observer.next(cart);
        observer.complete();
      });
    }
  }

  saveLocalCartToBackend(userId: number): void {
    const localCart = this.loadLocalCart();
    const uniqueProducts = new Set<number>();

    localCart.forEach((cartItem) => {
      if (!uniqueProducts.has(cartItem.productId)) {
        const params = new HttpParams()
          .set('userId', userId.toString())
          .set('productId', cartItem.productId.toString())
          .set('quantity', cartItem.quantity.toString());

        this.httpClient
          .post(`${this.apiUrl}/cart/addToCart`, null, { params })
          .pipe(
            catchError((error) => {
              console.error('Error adding product to cart:', error);
              return throwError(() => error);
            })
          )
          .subscribe(() => {
            console.log('Cart item added to backend successfully.');
          });

        uniqueProducts.add(cartItem.productId); // Éviter les doublons
      }
    });
  }

  updateCartItem(productId: number, newQuantity: number): Observable<void> {
    if (this.userId !== null) {
      // Si l'utilisateur est connecté, mettre à jour le panier du backend
      const params = new HttpParams().set(
        'newQuantity',
        newQuantity.toString()
      );

      return this.httpClient
        .put<void>(
          `${this.apiUrl}/cart/${this.userId}/products/${productId}`,
          null,
          {
            params,
          }
        )
        .pipe(
          catchError((error) => {
            console.error(
              'Erreur lors de la mise à jour du panier dans le backend:',
              error
            );
            return throwError(() => error);
          }),
          tap(() => {
            console.log('Mise à jour du panier du backend réussie');
            this.cartUpdated.emit(); // Émettre l'événement de mise à jour
          })
        );
    } else {
      // Si l'utilisateur n'est pas connecté, mettre à jour le panier local
      let localCart = this.loadLocalCart(); // Charger le panier local

      const itemIndex = localCart.findIndex(
        (item) => item.productId === productId
      );

      if (itemIndex !== -1) {
        // Mettre à jour la quantité du produit dans le panier local
        localCart[itemIndex].quantity = newQuantity;

        // Sauvegarder le panier local
        this.saveLocalCart(localCart);

        this.cartUpdated.emit(); // Émettre l'événement de mise à jour
      } else {
        console.warn('Produit non trouvé dans le panier local.');
      }

      return of();
    }
  }

  // Event for cart updates
  getCartUpdatedEvent(): Observable<void> {
    return this.cartUpdated.asObservable();
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
  // Méthode pour vider le panier local
  private clearLocalCart(): void {
    this.storageService.clean();
    this.localCartSubject.next([]);
  }
}
