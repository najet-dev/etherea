import { Injectable } from '@angular/core';
import {
  Observable,
  catchError,
  map,
  of,
  switchMap,
  tap,
  throwError,
} from 'rxjs';
import { environment } from 'src/environments/environment';
import { AuthService } from './auth.service';
import { SignupRequest } from '../components/models/signupRequest.model';
import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders,
} from '@angular/common/http';
import { StorageService } from './storage.service';
import { UpdateEmailRequest } from '../components/models/updateEmailRequest.model';
import { UpdatePasswordRequest } from '../components/models/updatePasswordRequest.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  apiUrl = environment.apiUrl;

  constructor(
    private authService: AuthService,
    private storageService: StorageService,
    private httpClient: HttpClient
  ) {}

  /**
   * Récupérer dynamiquement l'utilisateur connecté avec son ID
   */
  getCurrentUserDetails(): Observable<SignupRequest | null> {
    return this.authService.getCurrentUser().pipe(
      switchMap((user) => {
        if (!user || !user.id) {
          console.warn('Aucun utilisateur authentifié.');
          return of(null);
        }
        return this.getUserDetails(user.id);
      }),
      catchError((error) => {
        console.error(
          "Erreur lors de la récupération des détails de l'utilisateur:",
          error
        );
        return of(null);
      })
    );
  }

  getUserDetails(userId: number): Observable<SignupRequest | null> {
    return this.httpClient
      .get<SignupRequest>(`${this.apiUrl}/users/${userId}`)
      .pipe(
        catchError((error) => {
          console.error(
            "Erreur lors de la récupération des détails de l'utilisateur:",
            error
          );
          return of(null);
        })
      );
  }

  getAllUsers(page: number, size: number) {
    return this.httpClient
      .get<{
        content: SignupRequest[];
        totalElements: number;
        totalPages: number;
      }>(`${this.apiUrl}/users?page=${page}&size=${size}`)
      .pipe(
        tap((response) => console.log('API Response:', response)),
        catchError((error) => {
          console.error(
            'Erreur lors de la récupération des utilisateurs:',
            error
          );
          return throwError(
            () => new Error('Impossible de récupérer les utilisateurs.')
          );
        })
      );
  }

  deleteUser(userId: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.apiUrl}/users/${userId}`).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('Erreur lors de la suppression du produit:', error);
        return throwError(
          () =>
            new Error('Impossible de supprimer le produit. Veuillez réessayer.')
        );
      })
    );
  }
  updateEmail(updateEmailRequest: UpdateEmailRequest): Observable<string> {
    const url = `${this.apiUrl}/users/update-email`;
    const token = this.storageService.getToken();

    if (!token) {
      return throwError(
        () =>
          new Error('Utilisateur non authentifié. Veuillez vous reconnecter.')
      );
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    });

    return this.httpClient
      .put<string>(url, updateEmailRequest, { headers })
      .pipe(
        tap((response) =>
          console.log('Email mis à jour avec succès:', response)
        ),
        catchError((error) => {
          let errorMessage =
            "Erreur inconnue lors de la mise à jour de l'email.";
          if (error.status === 400) {
            errorMessage =
              "L'email actuel ne correspond pas à celui enregistré.";
          } else if (error.status === 401) {
            errorMessage = 'Session expirée. Veuillez vous reconnecter.';
          } else if (error.status === 500) {
            errorMessage = 'Erreur serveur. Veuillez réessayer plus tard.';
          }
          console.error('Erreur mise à jour email :', error);
          return throwError(() => new Error(errorMessage));
        })
      );
  }
  updatePassword(
    updatePasswordRequest: UpdatePasswordRequest
  ): Observable<string> {
    const url = `${this.apiUrl}/users/update-password`;
    const token = this.storageService.getToken();

    if (!token) {
      return throwError(
        () =>
          new Error('Utilisateur non authentifié. Veuillez vous reconnecter.')
      );
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    });
    updatePasswordRequest.token = token;

    return this.httpClient
      .put<string>(url, updatePasswordRequest, { headers })
      .pipe(
        tap(() => console.log('Mot de passe mis à jour avec succès')),
        catchError((error) => {
          let errorMessage =
            'Erreur inconnue lors de la mise à jour du mot de passe.';
          if (error.status === 400) {
            errorMessage = "L'ancien mot de passe est incorrect.";
          } else if (error.status === 401) {
            errorMessage = 'Session expirée. Veuillez vous reconnecter.';
          } else if (error.status === 500) {
            errorMessage = 'Erreur serveur. Veuillez réessayer plus tard.';
          }
          console.error('Erreur mise à jour mot de passe :', error);
          return throwError(() => new Error(errorMessage));
        })
      );
  }
}
