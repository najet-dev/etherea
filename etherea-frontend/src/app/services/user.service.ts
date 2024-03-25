import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from 'src/environments/environment';
import { HttpClient } from '@angular/common/http';
import { AuthService } from './auth.service';

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
    return this.authService
      .getCurrentUser()
      .pipe(map((user) => (user ? user.id : null)));
  }
}
