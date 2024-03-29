import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Router } from '@angular/router';

const TOKEN_KEY = 'auth-token'; // Utilisez TOKEN_KEY pour stocker le token JWT

@Injectable({
  providedIn: 'root',
})
export class StorageService {
  private isLoggedInSubject: BehaviorSubject<boolean> =
    new BehaviorSubject<boolean>(false);

  constructor(private router: Router) {
    this.isLoggedInSubject.next(this.isLoggedIn());
  }

  saveToken(token: string): void {
    window.localStorage.setItem(TOKEN_KEY, token);
    this.isLoggedInSubject.next(true);
  }

  getToken(): string | null {
    const token = window.localStorage.getItem(TOKEN_KEY);
    return token;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  isLoggedInObservable(): Observable<boolean> {
    return this.isLoggedInSubject.asObservable();
  }

  clean(): void {
    window.localStorage.clear();
    this.isLoggedInSubject.next(false);
  }
}
