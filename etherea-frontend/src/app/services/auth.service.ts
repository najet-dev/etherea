import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { StorageService } from './storage.service';
import { environment } from 'src/environments/environment';
import { SigninRequest } from '../components/models/signinRequest.moodel';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  apiUrl = environment.apiUrl;

  AuthenticatedUser$ = new BehaviorSubject<SigninRequest | null>(null);

  constructor(
    private httpClient: HttpClient,
    private storageService: StorageService,
    private router: Router
  ) {}

  login(signinData: SigninRequest): Observable<SigninRequest> {
    return this.httpClient
      .post<SigninRequest>(`${this.apiUrl}/api/auth/signin`, signinData, {
        withCredentials: true,
      })
      .pipe(
        catchError((err) => {
          console.log(err);
          let errorMessage = 'An unknown error occurred during login!';
          // Handle specific login errors if needed
          return throwError(() => new Error(errorMessage));
        }),
        tap((signin) => {
          this.storageService.saveUser(signin);
          this.AuthenticatedUser$.next(signin);
        })
      );
  }
}
