import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { StorageService } from './storage.service';
import { environment } from 'src/environments/environment';
import { SigninRequest } from '../components/models/signinRequest.moodel';
import { User } from '../components/models/user.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  apiUrl = environment.apiUrl;

  AuthenticatedUser$ = new BehaviorSubject<User | null>(null);

  constructor(
    private httpClient: HttpClient,
    private storageService: StorageService
  ) {}

  // signup(signupData: SignupRequest): Observable<User> {
  //   return this.httpClient.post<User>(`${this.apiUrl}/api/auth/signup`, signupData).pipe(
  //     catchError((err) => {
  //       console.log(err);
  //       let errorMessage = 'An unknown error occurred during signup!';
  //       // Handle specific signup errors if needed
  //       return throwError(() => new Error(errorMessage));
  //     })
  //   );
  // }

  login(signinData: SigninRequest): Observable<User> {
    return this.httpClient
      .post<User>(`${this.apiUrl}/api/auth/signin`, signinData, {
        withCredentials: true,
      })
      .pipe(
        catchError((err) => {
          console.log(err);
          let errorMessage = 'An unknown error occurred during login!';
          // Handle specific login errors if needed
          return throwError(() => new Error(errorMessage));
        }),
        tap((user) => {
          this.storageService.saveUser(user);
          this.AuthenticatedUser$.next(user);
        })
      );
  }
}
