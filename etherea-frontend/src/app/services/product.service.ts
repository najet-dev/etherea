import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Product } from '../components/models/product.model';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  apiUrl = environment.apiUrl;

  constructor(private httpClient: HttpClient) {}

  getProducts(limit: number = 0): Observable<Product[]> {
    const url =
      limit > 0
        ? `${this.apiUrl}/products?limit=${limit}`
        : `${this.apiUrl}/products`;
    return this.httpClient.get<Product[]>(url).pipe(
      catchError((error) => {
        console.error('Error fetching products:', error);
        console.error('Failed to load products. Please try again later.');
        return [];
      })
    );
  }

  getProductsByType(
    type: string,
    page: number,
    size: number
  ): Observable<Product[]> {
    const url = `${this.apiUrl}/products/type`;
    let params = new HttpParams();
    params = params.append('type', type);
    params = params.append('page', page.toString());
    params = params.append('size', size.toString());

    return this.httpClient.get<Product[]>(url, { params }).pipe(
      catchError((error) => {
        console.error('Error fetching products:', error);
        return throwError(() => error);
      })
    );
  }

  getProductById(id: number): Observable<Product> {
    return this.httpClient.get<Product>(`${this.apiUrl}/products/${id}`);
  }
}
