import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { IProduct } from '../components/models/i-product.model';

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

  getProductsByType(
    type: string,
    page: number,
    size: number
  ): Observable<IProduct[]> {
    const url = `${this.apiUrl}/products/type`;
    let params = new HttpParams();
    params = params.append('type', type);
    params = params.append('page', page.toString());
    params = params.append('size', size.toString());

    return this.httpClient.get<IProduct[]>(url, { params }).pipe(
      catchError((error) => {
        console.error('Error fetching products:', error);
        return throwError(() => error);
      })
    );
  }

  getProductById(id: number): Observable<IProduct> {
    return this.httpClient.get<IProduct>(`${this.apiUrl}/products/${id}`);
  }
}
