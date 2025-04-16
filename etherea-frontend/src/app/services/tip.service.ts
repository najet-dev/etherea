import { Injectable } from '@angular/core';
import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders,
} from '@angular/common/http';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { Tip } from '../components/models/tip.model';
import { environment } from 'src/environments/environment';
import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root',
})
export class TipService {
  apiUrl = environment.apiUrl;

  constructor(
    private httpClient: HttpClient,
    private storageService: StorageService
  ) {}

  // Récupérer tous les conseils (avec pagination)
  getAlltips(
    page: number,
    size: number
  ): Observable<{ content: Tip[]; totalElements: number; totalPages: number }> {
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

  // Ajouter un nouveau conseil (avec ou sans image)
  addTip(tip: Tip, image: File): Observable<Tip> {
    const formData = new FormData();
    formData.append('image', image, image.name);

    // Ajoute le JSON sous forme de texte
    formData.append('tip', JSON.stringify(tip));

    return this.httpClient.post<Tip>(`${this.apiUrl}/tips/add`, formData).pipe(
      catchError((error) => {
        console.error("Erreur lors de l'ajout du conseil :", error);
        return throwError(() => error);
      })
    );
  }

  // Mettre à jour un conseil (avec ou sans image)
  updateTip(updateTip: Partial<Tip>, image?: File): Observable<Tip> {
    const formData = new FormData();

    // Ajout de l'image
    if (image) {
      formData.append('image', image, image.name);
    }

    const filteredProduct = Object.fromEntries(
      Object.entries(updateTip).filter(
        ([_, value]) => value !== undefined && value !== null && value !== ''
      )
    );

    formData.append('tip', JSON.stringify(filteredProduct));

    return this.httpClient
      .put<Tip>(`${this.apiUrl}/tips/update`, formData)
      .pipe(
        catchError((error) => {
          console.error('Erreur lors de la mise à jour du produit:', error);
          return throwError(() => error);
        })
      );
  }

  deleteTip(tipId: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.apiUrl}/tips/${tipId}`).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('Erreur lors de la suppression du produit:', error);
        return throwError(
          () =>
            new Error('Impossible de supprimer le produit. Veuillez réessayer.')
        );
      })
    );
  }
}
