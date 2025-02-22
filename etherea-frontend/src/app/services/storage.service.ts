import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Router } from '@angular/router';

const TOKEN_KEY = 'auth-token';

@Injectable({
  providedIn: 'root',
})
export class StorageService {
  private isLoggedInSubject: BehaviorSubject<boolean>; // Subject BehaviorSubject pour suivre l'état de connexion

  constructor(private router: Router) {
    this.isLoggedInSubject = new BehaviorSubject<boolean>(this.isLoggedIn()); // Initialisation du BehaviorSubject avec l'état de connexion actuel
  }

  // Méthode pour sauvegarder le token JWT dans le stockage local
  saveToken(token: string): void {
    this.setItem(TOKEN_KEY, token);
    this.setLoggedIn(true); // Définit l'utilisateur comme connecté
  }
  // Méthode pour récupérer le token JWT depuis le stockage local
  getToken(): string | null {
    const token = localStorage.getItem(TOKEN_KEY);
    console.log('Token récupéré :', token); // Vérification
    return token;
  }

  // Méthode pour supprimer le token JWT du stockage local
  removeToken(): void {
    this.removeItem(TOKEN_KEY);
    this.setLoggedIn(false); // Définit l'utilisateur comme déconnecté
  }
  // Méthode pour vérifier si l'utilisateur est connecté
  isLoggedIn(): boolean {
    return !!this.getToken(); // Vérifie si un token est présent dans le stockage local
  }
  // Méthode pour définir l'état de connexion de l'utilisateur
  setLoggedIn(isLoggedIn: boolean): void {
    this.isLoggedInSubject.next(isLoggedIn); // Émet la nouvelle valeur de l'état de connexion via le BehaviorSubject
  }
  // Méthode pour obtenir un Observable de l'état de connexion
  isLoggedInObservable(): Observable<boolean> {
    return this.isLoggedInSubject.asObservable(); // Retourne l'Observable du BehaviorSubject de l'état de connexion
  }
  // Méthode pour déconnecter l'utilisateur
  logout(): void {
    this.removeToken();
    this.router.navigate(['/signin']);
  }
  // Méthode pour nettoyer l'état de connexion
  clean(): void {
    this.setLoggedIn(false); // Définit l'utilisateur comme déconnecté
  }
  // Méthode pour récupérer un élément du stockage local
  getItem(key: string): string | null {
    return localStorage.getItem(key);
  }
  // Méthode pour définir un élément dans le stockage local
  setItem(key: string, value: string): void {
    localStorage.setItem(key, value);
  }
  // Méthode pour supprimer un élément du stockage local
  removeItem(key: string): void {
    localStorage.removeItem(key);
  }
}
