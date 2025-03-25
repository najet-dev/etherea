import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of, tap, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Product } from '../components/models/Product.model';
import { StorageService } from './storage.service';
import { UpdateProduct } from '../components/models/updateProduct.model';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  apiUrl = environment.apiUrl;

  constructor(
    private httpClient: HttpClient,
    private storageService: StorageService
  ) {}

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
    console.log('Fetching product with ID:', id);
    return this.httpClient.get<Product>(`${this.apiUrl}/products/${id}`).pipe(
      tap((product) => {
        console.log('Fetched product from API:', product);
      }),
      catchError((error) => {
        console.error('Error fetching product from API:', error);
        return of();
      })
    );
  }

  addProduct(product: Product, image: File): Observable<Product> {
    const formData = new FormData();
    formData.append('image', image, image.name);

    //ajoute le JSON sous forme de texte
    formData.append('product', JSON.stringify(product));

    const token = this.storageService.getToken(); // Récupérer le token JWT

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`, // token JWT ajouter
    });

    return this.httpClient
      .post<Product>(`${this.apiUrl}/products/add`, formData, { headers })
      .pipe(
        catchError((error) => {
          console.error('Error adding product:', error);
          return throwError(() => error);
        })
      );
  }
  updateProduct(updatepPoduct: Product, image: File): Observable<Product> {
    const formData = new FormData();
    formData.append('image', image, image.name);

    //ajoute le JSON sous forme de texte
    formData.append('product', JSON.stringify(updatepPoduct));

    const token = this.storageService.getToken(); // Récupérer le token JWT

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`, // token JWT ajouter
    });

    return this.httpClient
      .put<UpdateProduct>(`${this.apiUrl}/products/update`, formData, {
        headers,
      })
      .pipe(
        catchError((error) => {
          console.error('Error adding product:', error);
          return throwError(() => error);
        })
      );
  }
}
