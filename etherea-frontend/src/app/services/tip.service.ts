import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { Tip } from '../components/models/tip.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class TipService {
  apiUrl = environment.apiUrl;

  constructor(private httpClient: HttpClient) {}

  // Récupérer tous les conseils (avec pagination)
  getAlltips(
    page: number = 0,
    size: number = 5
  ): Observable<{
    content: Tip[];
    totalElements: number;
    totalPages: number;
  }> {
    return this.httpClient
      .get<{ content: Tip[]; totalElements: number; totalPages: number }>(
        `${this.apiUrl}/tips?page=${page}&size=${size}`
      )
      .pipe(
        tap((response) => console.log('API Response:', response)),
        catchError((error) => {
          console.error('Erreur lors de la récupération des conseils:', error);
          return throwError(
            () => new Error('Impossible de récupérer les conseils.')
          );
        })
      );
  }

  getTipById(id: number): Observable<Tip> {
    return this.httpClient.get<Tip>(`${this.apiUrl}/tips/${id}`);
  }
}
