import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders,
  HttpParams,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of, tap, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Product } from '../components/models/product.model';
import { StorageService } from './storage.service';

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
  updateProduct(
    updateProduct: Partial<Product>,
    image?: File
  ): Observable<Product> {
    const formData = new FormData();

    // Ajout de l'image
    if (image) {
      formData.append('image', image, image.name);
    }

    // Filtrer les champs vides pour ne pas envoyer des données inutiles
    const filteredProduct = Object.fromEntries(
      Object.entries(updateProduct).filter(
        ([_, value]) => value !== undefined && value !== null && value !== ''
      )
    );

    // Ajout du produit en JSON
    formData.append('product', JSON.stringify(filteredProduct));

    const token = this.storageService.getToken(); // Récupérer le token JWT

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`, // Ajout du token JWT
    });

    return this.httpClient
      .put<Product>(`${this.apiUrl}/products/update`, formData, {
        headers,
      })
      .pipe(
        catchError((error) => {
          console.error('Erreur lors de la mise à jour du produit:', error);
          return throwError(() => error);
        })
      );
  }
  deleteProduct(productId: number): Observable<void> {
    const token = this.storageService.getToken(); // Récupérer le token JWT

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`, // Ajouter le token JWT
    });

    return this.httpClient
      .delete<void>(`${this.apiUrl}/products/${productId}`, { headers })
      .pipe(
        catchError((error: HttpErrorResponse) => {
          console.error('Erreur lors de la suppression du produit:', error);
          return throwError(
            () =>
              new Error(
                'Impossible de supprimer le produit. Veuillez réessayer.'
              )
          );
        })
      );
  }
}
