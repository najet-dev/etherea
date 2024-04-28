import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Router } from '@angular/router';
import { Cart } from '../components/models/cart.model';

const TOKEN_KEY = 'auth-token'; // Utilisez TOKEN_KEY pour stocker le token JWT
const cartKey = 'cartItems'; // Clé pour stocker le panier dans le stockage local

@Injectable({
  providedIn: 'root',
})
export class StorageService {
  private isLoggedInSubject: BehaviorSubject<boolean>;

  constructor(private router: Router) {
    this.isLoggedInSubject = new BehaviorSubject<boolean>(this.isLoggedIn());
  }

  saveToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token);
    this.isLoggedInSubject.next(true);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }
  removeToken(): void {
    localStorage.removeItem(TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
  setLoggedIn(isLoggedIn: boolean): void {
    this.isLoggedInSubject.next(isLoggedIn); // mettre à jour l'état
  }

  isLoggedInObservable(): Observable<boolean> {
    return this.isLoggedInSubject.asObservable();
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    this.isLoggedInSubject.next(false);
    this.router.navigate(['/signin']); // Redirection vers la page de connexion
  }

  clean(): void {
    localStorage.removeItem(cartKey); // Supprimer uniquement le panier local
    this.isLoggedInSubject.next(false);
  }

  get(key: string): any {
    const value = localStorage.getItem(key);
    return value ? JSON.parse(value) : null;
  }

  set(key: string, value: Cart): void {
    localStorage.setItem(key, JSON.stringify(value));
  }

  remove(key: string): void {
    localStorage.removeItem(key);
  }

  // saveLocalCart(cart: Cart): void {
  //   this.set(cartKey, cart);
  // }
  saveLocalCart(cart: Cart[]): void {
    const cartJson = JSON.stringify(cart);
    this.setItem(cartKey, cartJson);
  }

  // loadLocalCart(): Cart[] {
  //   return this.get(cartKey);
  // }
  loadLocalCart(): Cart[] {
    const cartJson = this.getItem(cartKey);
    return cartJson ? JSON.parse(cartJson) : [];
  }

  getItem(key: string): string | null {
    return localStorage.getItem(key);
  }

  setItem(key: string, value: string): void {
    localStorage.setItem(key, value);
  }
}
