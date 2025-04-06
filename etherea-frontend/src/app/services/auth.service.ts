import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';
import { StorageService } from './storage.service';
import { environment } from 'src/environments/environment';
import { SigninRequest } from '../components/models/signinRequest.model';
import { Router } from '@angular/router';
import { SignupRequest } from '../components/models/signupRequest.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = environment.apiUrl;
  AuthenticatedUser$ = new BehaviorSubject<SigninRequest | null>(null);

  constructor(
    private httpClient: HttpClient,
    private storageService: StorageService,
    private router: Router
  ) {}

  signup(signupData: SignupRequest): Observable<SignupRequest> {
    return this.httpClient
      .post<SignupRequest>(`${this.apiUrl}/api/auth/signup`, signupData)
      .pipe(
        catchError((error) => {
          console.error('An error occurred during signup:', error);
          return throwError(() => new Error("Erreur lors de l'inscription."));
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
        switchMap((signin) => {
          console.log('Signin response:', signin); // Log pour voir les rôles

          if (!signin.accessToken || !this.isValidJwt(signin.accessToken)) {
            console.error('Token JWT invalide.');
            return throwError(() => new Error('Token JWT invalide.'));
          }

          this.storageService.saveToken(signin.accessToken);
          this.AuthenticatedUser$.next(signin);

          // Vérification du rôle admin
          const isAdmin = signin.roles.includes('ROLE_ADMIN');
          console.log(
            isAdmin
              ? 'User signed in with ADMIN role.'
              : 'User signed in without ADMIN role.'
          );

          // Redirection vers le tableau de bord de l'admin si l'utilisateur a le rôle "ADMIN"
          if (isAdmin) {
            this.router.navigate(['/admin/admin-dashboard']);
          } else {
            console.log('Redirection vers /');
            this.router.navigate(['/']);
          }

          return of(signin);
        })
      );
  }

  private isValidJwt(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return !!payload && typeof payload === 'object';
    } catch (e) {
      return false;
    }
  }

  isAdmin(): boolean {
    const user = this.AuthenticatedUser$.getValue();
    console.log('User:', user);
    if (user && user.roles) {
      return user.roles.includes('ROLE_ADMIN');
    }
    return false;
  }

  logout(): Observable<void> {
    const token = this.storageService.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.httpClient
      .post<void>(
        `${this.apiUrl}/api/auth/logout`,
        {},
        {
          headers: new HttpHeaders().set(
            'Authorization',
            `Bearer ${this.storageService.getToken()}`
          ),
        }
      )
      .pipe(
        tap(() => {
          this.storageService.removeToken();
          this.AuthenticatedUser$.next(null);
          this.router.navigate(['/signin']);
        }),
        catchError(() => {
          this.storageService.clean();
          this.router.navigate(['/signin']);
          return of();
        })
      );
  }

  getCurrentUser(): Observable<SigninRequest | null> {
    return this.AuthenticatedUser$.asObservable().pipe(
      tap((user) => {
        console.log('User from AuthenticatedUser$:', user); // Log pour vérifier l'utilisateur
        if (!user) {
          console.error('Aucun utilisateur authentifié');
        }
      }),
      catchError((error) => {
        console.error(
          'Erreur lors de la récupération de l’utilisateur actuel :',
          error
        );
        return of(null);
      })
    );
  }
}
