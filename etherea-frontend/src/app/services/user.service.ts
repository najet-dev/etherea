import { Injectable } from '@angular/core';
import { Observable, catchError, map, of, tap } from 'rxjs';
import { environment } from 'src/environments/environment';
import { AuthService } from './auth.service';
import { SignupRequest } from '../components/models/SignupRequest.model';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  apiUrl = environment.apiUrl;

  constructor(
    private authService: AuthService,
    private httpClient: HttpClient
  ) {}

  getCurrentUser(): Observable<number | null> {
    return this.authService.getCurrentUser().pipe(
      tap((user) => {
        if (!user) {
          console.warn('Aucun utilisateur trouvé.');
        }
      }),
      map((user) => (user ? user.id : null)),
      catchError((error) => {
        console.error("Erreur lors de l'obtention de l'utilisateur:", error);
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
}
