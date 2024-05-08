import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Favorite } from '../components/models/favorite.model';

@Injectable()
export class FavoriteService {
  apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getUserFavorites(userId: number): Observable<Favorite[]> {
    return this.http.get<Favorite[]>(`${this.apiUrl}/favorites/${userId}`).pipe(
      catchError(() => {
        return throwError(
          () => new Error('Something went wrong while fetching user favorites.')
        );
      })
    );
  }

  addFavorite(userId: number, productId: number): Observable<string> {
    return this.http
      .post<string>(`${this.apiUrl}/favorites/${userId}/${productId}`, {})
      .pipe(
        catchError((error: HttpErrorResponse) => {
          return throwError(
            () => new Error('Failed to add product to favorites.')
          );
        })
      );
  }

  removeFavorite(userId: number, productId: number): Observable<string> {
    return this.http
      .delete<string>(`${this.apiUrl}/favorites/${userId}/${productId}`)
      .pipe(
        catchError(() => {
          return throwError(
            () => new Error('Failed to remove product from favorites.')
          );
        })
      );
  }
}
