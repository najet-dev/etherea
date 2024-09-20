import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { IProduct, ProductType } from '../components/models/i-product';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  apiUrl = environment.apiUrl;

  constructor(private httpClient: HttpClient) {}

  // Récupère les produits avec un nombre limité
  getProducts(limit: number = 0): Observable<IProduct[]> {
    const url =
      limit > 0
        ? `${this.apiUrl}/products?limit=${limit}`
        : `${this.apiUrl}/products`;
    return this.httpClient.get<IProduct[]>(url).pipe(
      catchError((error) => {
        console.error('Error fetching products:', error);
        return throwError(
          () => new Error('Failed to load products. Please try again later.')
        );
      })
    );
  }

  // Récupère les produits en fonction du type (FACE ou HAIR) avec pagination
  getProductsByType(
    type: ProductType, // Utilisation de l'enum ProductType
    page: number,
    size: number
  ): Observable<IProduct[]> {
    const url = `${this.apiUrl}/products/type`;
    let params = new HttpParams()
      .set('type', type)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.httpClient.get<IProduct[]>(url, { params }).pipe(
      catchError((error) => {
        console.error('Error fetching products by type:', error);
        return throwError(() => new Error('Failed to fetch products by type.'));
      })
    );
  }

  // Récupère un produit par son ID
  getProductById(productId: number): Observable<IProduct> {
    if (productId === undefined || productId === null) {
      console.error('Invalid product ID provided:', productId);
      return throwError(() => new Error('Invalid product ID'));
    }

    return this.httpClient.get<IProduct>(
      `${this.apiUrl}/products/${productId}`
    );
  }
}
