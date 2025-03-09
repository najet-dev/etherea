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

  // Méthode pour sauvegarder le token JWT dans le stockage local
  saveToken(token: string): void {
    if (!token) {
      console.error('Échec de l’enregistrement : le token est vide ou null');
      return;
    }
    this.setItem(TOKEN_KEY, token);
    this.setLoggedIn(true);
  }

  // Méthode pour générer un token temporaire (si l'utilisateur n'est pas connecté)
  generateTemporaryToken(): string {
    // Générer un token simple ou temporaire (par exemple, une clé aléatoire)
    const token = 'temp-' + Math.random().toString(36).substring(2); // Exemple de token temporaire
    this.saveToken(token);
    return token;
  }

  // Méthode pour récupérer le token JWT depuis le stockage local
  getToken(): string | null {
    const token = localStorage.getItem(TOKEN_KEY);
    return token;
  }

  // Méthode pour supprimer le token JWT du stockage local
  removeToken(): void {
    this.removeItem(TOKEN_KEY);
    this.setLoggedIn(false);
  }

  // Méthode pour vérifier si l'utilisateur est connecté
  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  // Méthode pour définir l'état de connexion de l'utilisateur
  setLoggedIn(isLoggedIn: boolean): void {
    this.isLoggedInSubject.next(isLoggedIn);
  }

  // Méthode pour obtenir un Observable de l'état de connexion
  isLoggedInObservable(): Observable<boolean> {
    return this.isLoggedInSubject.asObservable();
  }

  // Méthode pour déconnecter l'utilisateur
  logout(): void {
    this.removeToken();
    this.router.navigate(['/signin']);
  }

  // Méthode pour nettoyer l'état de connexion
  clean(): void {
    this.setLoggedIn(false);
  }

  // Méthodes de stockage local
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
