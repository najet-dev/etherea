import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { StorageService } from './storage.service';
import { Observable, catchError, throwError } from 'rxjs';
import { Cart } from '../components/models/cart.model';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  apiUrl = environment.apiUrl;

  constructor(
    private httpClient: HttpClient,
    private storageService: StorageService
  ) {}

  getUserCart(userId: number): Observable<Cart[]> {
    const url = `${this.apiUrl}/api/${userId}`;
    return this.httpClient.get<Cart[]>(url).pipe(
      catchError((error) => {
        console.error('Error while getting user cart:', error);
        return throwError(() => new Error('Error while getting user cart:'));
      })
    );
  }
}
