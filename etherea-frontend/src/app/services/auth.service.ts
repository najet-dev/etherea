import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { StorageService } from './storage.service';
import { environment } from 'src/environments/environment';
import { SigninRequest } from '../components/models/signinRequest.model';
import { Role, ERole } from '../components/models/role.model';

import { Router } from '@angular/router';
import { SignupRequest } from '../components/models/SignupRequest.model';
import { CartService } from './cart.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  apiUrl = environment.apiUrl;

  AuthenticatedUser$ = new BehaviorSubject<SigninRequest | null>(null);

  constructor(
    private httpClient: HttpClient,
    private storageService: StorageService,
    private router: Router,
    private cartService: CartService
  ) {}

  signup(signupData: SignupRequest): Observable<SignupRequest> {
    return this.httpClient
      .post<any>(`${this.apiUrl}/api/auth/signup`, signupData)
      .pipe(
        catchError((error) => {
          console.error('An error occurred during signup:', error);
          return throwError(() => error);
        })
      );
  }

  signin(signinData: SigninRequest): Observable<SigninRequest> {
    return this.httpClient
      .post<SigninRequest>(`${this.apiUrl}/api/auth/signin`, signinData, {
        withCredentials: true,
      })
      .pipe(
        catchError((error) => {
          let errorMessage =
            "Une erreur inconnue s'est produite lors de la connexion !";
          if (error.status === 401) {
            errorMessage = 'Identifiants invalides. Veuillez réessayer.';
          } else if (error.status === 403) {
            errorMessage = "Vous n'avez pas les autorisations nécessaires.";
          }
          console.error('Signin error:', error);
          return throwError(() => new Error(errorMessage));
        }),
        tap((signin) => {
          this.storageService.saveToken(signin.accessToken);
          this.AuthenticatedUser$.next(signin);
          console.log('User signed in successfully:', signin);
          // Vider et sauvegarder le panier local lorsque l'utilisateur se connecte
          this.cartService.clearLocalCart();
          console.log('Local cart cleared.');
        })
      );
  }

  logout(): void {
    // console.log('AuthService: Logging out');
    const token = this.storageService.getToken(); // Récupérer le token JWT depuis le stockage local

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`); // Ajouter le token JWT aux en-têtes

    this.httpClient
      .post(`${this.apiUrl}/api/auth/logout`, {}, { headers }) // Envoyer la demande de déconnexion avec les en-têtes contenant le token JWT
      .pipe(
        tap(() => {
          // console.log('User logged out successfully!');
          this.storageService.clean();
          this.AuthenticatedUser$.next(null);
          this.router.navigate(['/signin']);
        }),
        catchError((error) => {
          //console.error('An error occurred during logout:', error);
          // Même s'il y a une erreur lors de la déconnexion, nettoyez le stockage local et redirigez vers la page de connexion
          this.storageService.clean();
          this.AuthenticatedUser$.next(null);
          this.router.navigate(['/signin']);
          return throwError(() => error);
        })
      )
      .subscribe();
  }
  getCurrentUser(): Observable<SigninRequest | null> {
    return this.AuthenticatedUser$.asObservable();
  }
}
