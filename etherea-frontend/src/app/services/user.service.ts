import { Injectable } from '@angular/core';
import { Observable, catchError, map, of, tap } from 'rxjs';
import { environment } from 'src/environments/environment';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  apiUrl = environment.apiUrl;

  constructor(private authService: AuthService) {}

  getCurrentUserId(): Observable<number | null> {
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
}
