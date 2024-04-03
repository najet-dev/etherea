import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Router } from '@angular/router';

const TOKEN_KEY = 'auth-token'; // Utilisez TOKEN_KEY pour stocker le token JWT
const cartKey = 'cartItems'; // Clé pour stocker le panier dans le stockage local

@Injectable({
  providedIn: 'root',
})
export class StorageService {
  private isLoggedInSubject: BehaviorSubject<boolean>;

  constructor() {
    this.isLoggedInSubject = new BehaviorSubject<boolean>(this.isLoggedIn());
  }

  saveToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token);
    this.isLoggedInSubject.next(true);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  isLoggedInObservable(): Observable<boolean> {
    return this.isLoggedInSubject.asObservable();
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    this.isLoggedInSubject.next(false);
  }

  clean(): void {
    window.localStorage.clear();
    this.isLoggedInSubject.next(false);
  }

  get(key: string): any {
    const value = localStorage.getItem(key);
    return value ? JSON.parse(value) : null;
  }

  set(key: string, value: any): void {
    localStorage.setItem(key, JSON.stringify(value));
  }

  remove(key: string): void {
    localStorage.removeItem(key);
  }
}
