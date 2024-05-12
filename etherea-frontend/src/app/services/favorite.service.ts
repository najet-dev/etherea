import { Injectable } from '@angular/core';
import {
  HttpClient,
  HttpErrorResponse,
  HttpParams,
} from '@angular/common/http';
import { Observable, throwError, BehaviorSubject, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Favorite } from '../components/models/favorite.model';
import { IProduct } from '../components/models/i-product';
import { AuthService } from './auth.service';

@Injectable()
export class FavoriteService {
  apiUrl = environment.apiUrl;
  private favoritesSubject = new BehaviorSubject<number[]>([]);
  favorites$ = this.favoritesSubject.asObservable();
  userId: number | null = null;

  constructor(
    private httpClient: HttpClient,
    private authService: AuthService
  ) {}

  getUserFavorites(userId: number): Observable<Favorite[]> {
    return this.httpClient
      .get<Favorite[]>(`${this.apiUrl}/favorites/${userId}`)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          console.error('Error fetching user favorites:', error);
          return throwError(() => error);
        })
      );
  }

  addFavorite(userId: number, productId: number): Observable<Favorite> {
    const params = new HttpParams()
      .set('userId', userId.toString())
      .set('productId', productId.toString());

    return this.httpClient
      .post<Favorite>(`${this.apiUrl}/favorites/${userId}/${productId}`, null, {
        params,
      })
      .pipe(
        catchError((error: HttpErrorResponse) => {
          console.error('Error adding product to favorites:', error);
          return throwError(() => error);
        })
      );
  }

  updateFavorites(userId: number): Observable<Favorite[]> {
    return this.httpClient
      .put<Favorite[]>(`${this.apiUrl}/favorites/${userId}`, {})
      .pipe(
        catchError((error: HttpErrorResponse) => {
          console.error('Error updating favorites:', error);
          return throwError(() => error);
        })
      );
  }

  removeFavorite(userId: number, productId: number): Observable<string> {
    return this.httpClient
      .delete<string>(`${this.apiUrl}/favorites/${userId}/${productId}`)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          console.error('Error removing favorite:', error);
          return throwError(() => error);
        })
      );
  }
  toggleFavorite(product: IProduct): void {
    if (this.userId) {
      this.authService
        .getCurrentUser()
        .pipe(tap((user) => (this.userId = user ? user.id : null)))
        .subscribe();
      if (product.isFavorite) {
        this.removeFavorite(this.userId, product.id).subscribe(() => {
          product.isFavorite = false;
        });
      } else {
        this.addFavorite(this.userId, product.id).subscribe(() => {
          product.isFavorite = true;
        });
      }
    }
  }
  productsToFavorites(products: IProduct[]): Observable<IProduct[]> {
    if (this.userId) {
      return this.getUserFavorites(this.userId).pipe(
        map((favorites: Favorite[]) => {
          return products.map((product: IProduct) => ({
            ...product,
            isFavorite: favorites.some((fav) => fav.productId === product.id),
          }));
        }),
        catchError((error) => {
          console.error('Error fetching products:', error);
          console.error('Failed to load products. Please try again later.');
          return of([]);
        })
      );
    } else {
      return of(products);
    }
  }
}
