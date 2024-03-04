import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { IProduct } from '../components/models/i-product';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  apiUrl = environment.apiUrl;

  constructor(private httpClient: HttpClient) {}

  getProducts(limit: number = 0): Observable<IProduct[]> {
    const url =
      limit > 0
        ? `${this.apiUrl}/products?limit=${limit}`
        : `${this.apiUrl}/products`;
    return this.httpClient.get<IProduct[]>(url).pipe(
      catchError((error) => {
        console.error('Error fetching products:', error);
        console.error('Failed to load products. Please try again later.');
        return [];
      })
    );
  }

  getProductById(id: number): Observable<IProduct> {
    return this.httpClient.get<IProduct>(`${this.apiUrl}/products/${id}`);
  }

  incrementProductQuantity(productId: number): Observable<IProduct> {
    const url = `${this.apiUrl}/products/${productId}/increment`;
    return this.httpClient.post<IProduct>(url, null);
  }

  decrementProductQuantity(productId: number): Observable<IProduct> {
    const url = `${this.apiUrl}/products/${productId}/decrement`;
    return this.httpClient.post<IProduct>(url, null);
  }
}
