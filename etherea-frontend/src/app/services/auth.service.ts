import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { StorageService } from './storage.service';
import { environment } from 'src/environments/environment';
import { SigninRequest } from '../components/models/signinRequest.model';
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
    const token = this.storageService.getToken();

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    this.httpClient
      .post(`${this.apiUrl}/api/auth/logout`, {}, { headers })
      .pipe(
        tap(() => {
          this.storageService.clean();
          this.AuthenticatedUser$.next(null);
          this.router.navigate(['/signin']);
        }),
        catchError((error) => {
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
