import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { IProduct } from '../components/models/i-product';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  apiUrl = environment.apiUrl;

  constructor(private httpClient: HttpClient) {}

  getProducts(): Observable<IProduct[]> {
    return this.httpClient.get<IProduct[]>(`${this.apiUrl}/products`);
  }

  getProduitById(id: number): Observable<IProduct[]> {
    return this.httpClient.get<IProduct[]>(`${this.apiUrl}/${id}`);
  }
}
