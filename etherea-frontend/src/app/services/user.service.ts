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

  // getUserId(id: number): Observable<SigninRequest> {
  //   return this.httpClient.get<SigninRequest>(`${this.apiUrl}/users/${id}`);
  // }
  getCurrentUserId(): Observable<number | null> {
    return this.authService.getCurrentUser().pipe(
      map((user) => (user ? user.id : null)),
      catchError(() => of(null)) // Gérer les erreurs en renvoyant null si aucun utilisateur n'est connecté
    );
  }
}
