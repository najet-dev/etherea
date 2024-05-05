import { Injectable } from '@angular/core';
import { Observable, catchError, map, of } from 'rxjs';
import { environment } from 'src/environments/environment';
import { HttpClient } from '@angular/common/http';
import { AuthService } from './auth.service';
import { SigninRequest } from '../components/models/signinRequest.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  apiUrl = environment.apiUrl;

  constructor(
    private httpClient: HttpClient,
    private authService: AuthService
  ) {}

  getCurrentUserId(): Observable<number | null> {
    return this.authService.getCurrentUser().pipe(
      map((user) => (user ? user.id : null)), // Retourner l'id ou null
      catchError((error) => {
        console.error("Erreur lors de l'obtention de l'utilisateur:", error);
        return of(null); // En cas d'erreur, renvoyer null pour éviter des exceptions
      })
    );
  }
}
