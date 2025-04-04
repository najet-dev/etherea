import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';
import { StorageService } from './storage.service';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';
import { SigninRequest } from '../components/models/signinRequest.model';
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
        catchError((error) =>
          throwError(() => new Error(this.getErrorMessage(error)))
        ),
        tap((signin) => {
          if (!signin.accessToken || !this.isValidJwt(signin.accessToken)) {
            throw new Error('Token JWT invalide.');
          }
          this.storageService.saveToken(signin.accessToken);
          this.AuthenticatedUser$.next(signin);

          if (signin.roles.includes(Role.ROLE_ADMIN)) {
            this.router.navigate(['/admin/admin-dashboard']);
          } else {
            this.router.navigate(['/']);
          }
        })
      );
  }

  private isValidJwt(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return !!payload;
    } catch {
      return false;
    }
  }

  isAdmin(): boolean {
    return (
      this.AuthenticatedUser$.getValue()?.roles.includes(Role.ROLE_ADMIN) ||
      false
    );
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
    return this.AuthenticatedUser$.asObservable();
  }

  private getErrorMessage(error: any): string {
    if (error.status === 401) return 'Identifiants invalides.';
    if (error.status === 403)
      return "Vous n'avez pas les autorisations nécessaires.";
    return "Une erreur inconnue s'est produite.";
  }
}
