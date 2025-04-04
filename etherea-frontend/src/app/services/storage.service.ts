import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Router } from '@angular/router';

const TOKEN_KEY = 'auth-token';
@Injectable({
  providedIn: 'root',
})
export class StorageService {
  private isLoggedInSubject: BehaviorSubject<boolean>;

  constructor(private router: Router) {
    this.isLoggedInSubject = new BehaviorSubject<boolean>(this.isLoggedIn());
  }

  saveToken(token: string): void {
    if (!token || token.trim().length === 0) {
      console.error('Token invalide, enregistrement annulé.');
      return;
    }
    this.setItem(TOKEN_KEY, token);
    console.log('Token enregistré dans localStorage:', token);
    this.setLoggedIn(true);
  }

  getToken(): string | null {
    const token = localStorage.getItem(TOKEN_KEY);
    console.log('Token récupéré depuis localStorage:', token); // Log ajouté
    return token;
  }

  removeToken(): void {
    this.removeItem(TOKEN_KEY);
    this.setLoggedIn(false);
    console.log('Token supprimé');
  }

  isLoggedIn(): boolean {
    return !!this.getToken(); // Retourne true si le token est présent
  }

  setLoggedIn(isLoggedIn: boolean): void {
    this.isLoggedInSubject.next(isLoggedIn);
  }

  isLoggedInObservable(): Observable<boolean> {
    return this.isLoggedInSubject.asObservable();
  }

  logout(): void {
    this.removeToken();
    console.log('Utilisateur déconnecté');
    this.router.navigate(['/signin']);
  }

  clean(): void {
    this.setLoggedIn(false);
  }

  getItem(key: string): string | null {
    return localStorage.getItem(key);
  }

  setItem(key: string, value: string): void {
    localStorage.setItem(key, value);
  }

  removeItem(key: string): void {
    localStorage.removeItem(key);
  }
}
