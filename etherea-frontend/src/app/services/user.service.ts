import { Injectable } from '@angular/core';
import { Observable, catchError, map, of, tap } from 'rxjs';
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
      tap((user) => console.log('Current user in UserService:', user)), // Ajout du log pour vérifier l'utilisateur actuel
      map((user) => (user ? user.id : null)),
      catchError(() => of(null)) // Gérer les erreurs en renvoyant null si aucun utilisateur n'est connecté
    );
  }
}
