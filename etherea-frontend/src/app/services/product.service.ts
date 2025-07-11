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

  getAllProducts(
    page: number = 0,
    size: number = 10
  ): Observable<{
    content: Product[];
    totalElements: number;
    totalPages: number;
  }> {
    return this.httpClient
      .get<{ content: Product[]; totalElements: number; totalPages: number }>(
        `${this.apiUrl}/products?page=${page}&size=${size}`
      )
      .pipe(
        tap((response) => console.log('API Response:', response)),
        catchError((error) => {
          console.error('Erreur lors de la récupération des produits:', error);
          return throwError(
            () => new Error('Impossible de récupérer les produits.')
          );
        })
      );
  }
  getProductsByType(
    type: string,
    page: number = 0,
    size: number = 10
  ): Observable<{
    content: Product[];
    totalElements: number;
    totalPages: number;
    number: number;
  }> {
    const url = `${this.apiUrl}/products/type`;
    const params = new HttpParams()
      .set('type', type)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.httpClient
      .get<{
        content: Product[];
        totalElements: number;
        totalPages: number;
        number: number;
      }>(url, { params })
      .pipe(
        tap((response) => {
          console.log(`Produits de type "${type}" page ${page}:`, response);
        }),
        catchError((error) => {
          console.error(
            `Erreur lors de la récupération des produits du type "${type}" :`,
            error
          );
          return throwError(
            () => new Error('Impossible de récupérer les produits par type.')
          );
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
  getNewProducts(
    page: number = 0,
    size: number = 10
  ): Observable<{
    content: Product[];
    totalElements: number;
    totalPages: number;
  }> {
    return this.httpClient
      .get<{ content: Product[]; totalElements: number; totalPages: number }>(
        `${this.apiUrl}/products/newProduct?page=${page}&size=${size}`
      )
      .pipe(
        tap((response) => console.log('New products fetched:', response)),
        catchError((error) => {
          console.error('Error fetching new products:', error);
          return throwError(() => new Error('Failed to fetch new products.'));
        })
      );
  }

  // Méthode pour rechercher des produits par nom
  searchProductsByName(name: string): Observable<Product[]> {
    if (!name.trim()) {
      return of([]); // Ne pas envoyer de requête si la recherche est vide
    }

    const params = new HttpParams().set('name', name.trim());

    return this.httpClient
      .get<Product[]>(`${this.apiUrl}/products/search`, { params })
      .pipe(
        tap((response) => console.log('Réponse reçue:', response)),
        catchError((error) => {
          console.error('Erreur lors de la recherche:', error);
          return of([]); // Retourne un tableau vide en cas d'erreur
        })
      );
  }

  addProduct(product: Product, image: File): Observable<Product> {
    const formData = new FormData();
    formData.append('image', image, image.name);

    //ajoute le JSON sous forme de texte
    formData.append('product', JSON.stringify(product));

    return this.httpClient
      .post<Product>(`${this.apiUrl}/products/add`, formData)
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

    const filteredProduct = Object.fromEntries(
      Object.entries(updateProduct).filter(
        ([_, value]) => value !== undefined && value !== null && value !== ''
      )
    );

    formData.append('product', JSON.stringify(filteredProduct));

    return this.httpClient
      .put<Product>(`${this.apiUrl}/products/update`, formData)
      .pipe(
        catchError((error) => {
          console.error('Erreur lors de la mise à jour du produit:', error);
          return throwError(() => error);
        })
      );
  }

  deleteProduct(productId: number): Observable<void> {
    return this.httpClient
      .delete<void>(`${this.apiUrl}/products/${productId}`)
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
