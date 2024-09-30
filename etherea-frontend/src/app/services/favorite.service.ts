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
import { Product } from '../components/models/Product.model';
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
  ) {
    this.authService.getCurrentUser().subscribe((user) => {
      this.userId = user ? user.id : null;
      if (this.userId) {
        this.loadUserFavorites(this.userId);
      }
    });
  }

  loadUserFavorites(userId: number): void {
    this.getUserFavorites(userId).subscribe((favorites) => {
      this.favoritesSubject.next(favorites.map((fav) => fav.productId));
    });
  }

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
    return this.httpClient
      .post<Favorite>(`${this.apiUrl}/favorites/${userId}/${productId}`, null)
      .pipe(
        tap(() => {
          const currentFavorites = this.favoritesSubject.value;
          this.favoritesSubject.next([...currentFavorites, productId]);
        }),
        catchError((error: HttpErrorResponse) => {
          console.error('Error adding product to favorites:', error);
          return throwError(() => error);
        })
      );
  }

  removeFavorite(userId: number, productId: number): Observable<void> {
    return this.httpClient
      .delete<void>(`${this.apiUrl}/favorites/${userId}/${productId}`)
      .pipe(
        tap(() => {
          const currentFavorites = this.favoritesSubject.value;
          this.favoritesSubject.next(
            currentFavorites.filter((id) => id !== productId)
          );
        }),
        catchError((error: HttpErrorResponse) => {
          console.error('Error removing favorite:', error);
          return throwError(() => error);
        })
      );
  }

  toggleFavorite(product: Product): void {
    if (this.userId) {
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

  productsFavorites(products: Product[]): Observable<Product[]> {
    return this.favorites$.pipe(
      map((favoriteIds) => {
        return products.map((product) => {
          product.isFavorite = favoriteIds.includes(product.id);
          return product;
        });
      })
    );
  }
}
