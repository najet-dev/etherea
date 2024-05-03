import { EventEmitter, Injectable } from '@angular/core';
import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders,
  HttpParams,
} from '@angular/common/http';
import { EMPTY, Observable, of, throwError } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Cart } from '../components/models/cart.model';
import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private apiUrl = environment.apiUrl;
  userId: number | null = null; // Initialiser la propriété userId
  private cartKey = 'cartItems';

  cartUpdated: EventEmitter<void> = new EventEmitter<void>(); // Événement pour indiquer la mise à jour du panier

  constructor(
    private httpClient: HttpClient,
    private storageService: StorageService
  ) {}

  // Méthodes pour interagir avec le backend
  getCartItems(userId: number): Observable<Cart[]> {
    // Si l'utilisateur est connecté, obtenir les éléments du panier depuis le backend
    if (userId) {
      return this.httpClient.get<Cart[]>(`${this.apiUrl}/cart/${userId}`).pipe(
        catchError((error) => {
          console.error(
            'Erreur lors de la récupération des éléments du panier :',
            error
          );
          return throwError(() => error);
        })
      );
    } else {
      // Si l'utilisateur n'est pas connecté, renvoyer simplement les éléments du panier du stockage local
      return of(this.storageService.loadLocalCart());
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
            console.error(
              "Erreur lors de l'ajout du produit au panier :",
              error
            );
            return throwError(() => error);
          })
        );
    } else {
      let localCart = this.storageService.loadLocalCart();
      localCart.push(cart);
      this.storageService.saveLocalCart(localCart);
      this.cartUpdated.next(); // Émettre un événement pour indiquer que le panier a été mis à jour
      return of(cart);
    }
  }

  updateCartItem(item: Cart): Observable<void> {
    if (this.userId !== null) {
      const url = `${this.apiUrl}/cart/${this.userId}/products/${item.productId}?newQuantity=${item.quantity}`;
      return this.httpClient.put<void>(url, null).pipe(
        tap(() => {
          console.log(
            "Quantité de l'élément de panier mise à jour avec succès"
          );
          this.cartUpdated.next(); // Émettre un événement pour indiquer que le panier a été mis à jour
        }),
        catchError((error) => {
          console.error(
            "Erreur lors de la mise à jour de la quantité d'article du panier :",
            error
          );
          return throwError(() => error);
        })
      );
    } else {
      // Si l'utilisateur n'est pas connecté, mettre à jour le panier localement
      let localCart = this.storageService.loadLocalCart();
      const index = localCart.findIndex(
        (cartItem) => cartItem.productId === item.productId
      );
      if (index !== -1) {
        localCart[index].quantity = item.quantity;
        this.storageService.saveLocalCart(localCart);
        // Émettre un événement pour indiquer que le panier a été mis à jour
        this.cartUpdated.next();
        return EMPTY; // Retourner un observable vide
      } else {
        // Si le produit n'est pas déjà présent dans le panier local, ne rien faire
        return EMPTY; // Retourner un observable vide
      }
    }
  }

  // Nouvelle méthode pour synchroniser le panier avec le backend
  syncCartWithServer(userId: number): Observable<any> {
    const localCart = this.storageService.loadLocalCart();

    if (localCart.length === 0) {
      return of('Le panier local est vide, rien à synchroniser.');
    }

    return this.httpClient
      .post<any>(`${this.apiUrl}/cart/sync/${userId}`, localCart, {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
        }),
      })
      .pipe(
        tap((response) => {
          console.log(
            'Réponse du backend lors de la synchronisation du panier :',
            response
          );
          this.cartUpdated.next();
        }),
        catchError((error: HttpErrorResponse) => {
          console.error('Erreur lors de la synchronisation du panier :', error);
          return throwError(error);
        })
      );
  }

  // Supprimer un article du panier
  deleteCartItem(id: number): Observable<Cart | null> {
    if (this.userId !== null) {
      return this.httpClient.delete<Cart>(`${this.apiUrl}/cart/${id}`).pipe(
        catchError((error) => {
          console.error(
            "Erreur lors de la suppression de l'article du panier :",
            error
          );
          return throwError(() => error);
        })
      );
    } else {
      let localCart = this.storageService.loadLocalCart();
      const index = localCart.findIndex((item) => item.id === id);
      if (index !== -1) {
        localCart.splice(index, 1);
        this.storageService.saveLocalCart(localCart);
        this.cartUpdated.next(); // Émettre un événement pour indiquer que le panier a été mis à jour
      }
      return of(null);
    }
  }
}
