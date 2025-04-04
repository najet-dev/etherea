import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { StorageService } from './storage.service';
import { environment } from 'src/environments/environment';
import { SigninRequest } from '../components/models/signinRequest.model';
import { Router } from '@angular/router';
import { SignupRequest } from '../components/models/signupRequest.model';
import { Role } from '../components/models/role.enum';
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
  ) {
    this.loadUserFromToken();
  }

  private loadUserFromToken(): void {
    const token = this.storageService.getToken();
    console.log('Token récupéré dans loadUserFromToken:', token);

    if (token && this.isValidJwt(token)) {
      const payload = JSON.parse(atob(token.split('.')[1]));
      console.log('Payload JWT:', payload);

      const user: SigninRequest = {
        id: 0,
        username: payload.sub,
        accessToken: token,
        roles: payload.roles || [],
      };
      this.AuthenticatedUser$.next(user);
      this.redirectUserBasedOnRole(user);
    } else {
      console.log(
        'Token invalide ou inexistant, redirection vers la page de connexion'
      );
      this.router.navigate(['/signin']);
    }
  }
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
        tap((signin) => {
          console.log('Réponse du backend :', signin);

          if (!signin.accessToken || !this.isValidJwt(signin.accessToken)) {
            throw new Error('Token JWT invalide.');
          }

          this.storageService.saveToken(signin.accessToken);
          console.log(
            'Token après enregistrement :',
            this.storageService.getToken()
          );

          this.AuthenticatedUser$.next(signin);
          this.redirectUserBasedOnRole(signin);
        }),
        catchError((error) => {
          console.error('Erreur lors de la connexion:', error);
          return throwError(() => new Error('Échec de la connexion.'));
        })
      );
  }

  private redirectUserBasedOnRole(user: SigninRequest): void {
    if (user.roles.includes(Role.ROLE_ADMIN)) {
      console.log('Rôle admin détecté, redirection vers /admin');
      this.router.navigate(['/admin/admin-dashbord']);
    } else {
      console.log('Pas admin, redirection vers /');
      this.router.navigate(['/']);
    }
  }

  private isValidJwt(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiry = payload.exp * 1000;
      return expiry > Date.now();
    } catch {
      return false;
    }
  }

  logout(): Observable<void> {
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
