import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Router } from '@angular/router';
import { Cart } from '../components/models/cart.model';

const TOKEN_KEY = 'auth-token';
const CART_KEY = 'cartItems'; // Clé pour stocker le panier dans le stockage local

@Injectable({
  providedIn: 'root',
})
export class StorageService {
  private isLoggedInSubject: BehaviorSubject<boolean>;

  constructor(private router: Router) {
    this.isLoggedInSubject = new BehaviorSubject<boolean>(this.isLoggedIn());
  }

  saveToken(token: string): void {
    this.setItem(TOKEN_KEY, token);
    this.setLoggedIn(true);
  }

  getToken(): string | null {
    return this.getItem(TOKEN_KEY);
  }

  removeToken(): void {
    this.removeItem(TOKEN_KEY);
    this.setLoggedIn(false);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  setLoggedIn(isLoggedIn: boolean): void {
    this.isLoggedInSubject.next(isLoggedIn);
  }

  isLoggedInObservable(): Observable<boolean> {
    return this.isLoggedInSubject.asObservable();
  }

  logout(): void {
    this.removeToken(); // Supprime le token JWT
    this.router.navigate(['/signin']); // Redirection vers la page de connexion
  }

  clean(): void {
    this.removeItem(CART_KEY); // Supprimer uniquement le panier local
    this.setLoggedIn(false);
  }

  getItem(key: string): string | null {
    return localStorage.getItem(key); // Utilise la clé passée en paramètre
  }

  setItem(key: string, value: string): void {
    localStorage.setItem(key, value); // Utilise la clé passée en paramètre
  }

  removeItem(key: string): void {
    localStorage.removeItem(key); // Utilise la clé passée en paramètre
  }

  getLocalCart(): Cart[] {
    const cartJson = localStorage.getItem(CART_KEY); // Utilise la clé CART_KEY
    return cartJson ? JSON.parse(cartJson) : [];
  }

  saveLocalCart(cart: Cart[]): void {
    const cartJson = JSON.stringify(cart);
    this.setItem(CART_KEY, cartJson);
  }

  loadLocalCart(): Cart[] {
    const cartJson = this.getItem(CART_KEY); // Utilise la clé CART_KEY
    return cartJson ? JSON.parse(cartJson) : [];
  }
}
