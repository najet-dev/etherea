import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { StorageService } from './storage.service';
import { environment } from 'src/environments/environment';
import { SigninRequest } from '../components/models/signinRequest.model';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  apiUrl = environment.apiUrl;

  AuthenticatedUser$ = new BehaviorSubject<SigninRequest | null>(null);

  constructor(
    private httpClient: HttpClient,
    private storageService: StorageService,
    private router: Router
  ) {}

  login(signinData: SigninRequest): Observable<SigninRequest> {
    console.log('AuthService: Logging in', signinData);

    return this.httpClient
      .post<SigninRequest>(`${this.apiUrl}/api/auth/signin`, signinData, {
        withCredentials: true,
      })
      .pipe(
        catchError((err) => {
          console.log(err);
          let errorMessage = 'An unknown error occurred during login!';
          // Handle specific login errors if needed
          return throwError(() => new Error(errorMessage));
        }),
        tap((signin) => {
          this.storageService.saveToken(signin.accessToken); // Utilisez saveToken pour enregistrer le token JWT
          this.AuthenticatedUser$.next(signin);
        })
      );
  }

  logout(): void {
    console.log('AuthService: Logging out');

    const token = this.storageService.getToken(); // Récupérer le token JWT depuis le stockage local

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`); // Ajouter le token JWT aux en-têtes

    this.httpClient
      .post(`${this.apiUrl}/api/auth/logout`, {}, { headers }) // Envoyer la demande de déconnexion avec les en-têtes contenant le token JWT
      .pipe(
        tap(() => {
          console.log('User logged out successfully!');
          this.storageService.clean();
          this.AuthenticatedUser$.next(null);
          this.router.navigate(['/signin']);
        }),
        catchError((error) => {
          console.error('An error occurred during logout:', error);
          // Même s'il y a une erreur lors de la déconnexion, nettoyez le stockage local et redirigez vers la page de connexion
          this.storageService.clean();
          this.AuthenticatedUser$.next(null);
          this.router.navigate(['/signin']);
          return throwError(() => error);
        })
      )
      .subscribe();
  }
}
