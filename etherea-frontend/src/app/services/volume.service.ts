import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Volume } from '../components/models/volume.model';
import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root',
})
export class VolumeService {
  apiUrl = environment.apiUrl;

  constructor(
    private httpClient: HttpClient,
    private storageService: StorageService
  ) {}

  getVolumes(): Observable<Volume[]> {
    return this.httpClient.get<Volume[]>(`${this.apiUrl}/volumes`).pipe(
      tap((response) => console.log('API Response:', response)),
      catchError((error) => {
        console.error('Erreur lors de la récupération des volumes:', error);
        return throwError(
          () => new Error('Impossible de récupérer les volumes.')
        );
      })
    );
  }
  addVolume(productName: string, volume: Volume): Observable<Volume> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`, // Ajouter le token JWT
    });

    return this.httpClient
      .post<Volume>(`${this.apiUrl}/volumes/products/${productName}`, volume, {
        headers,
      })
      .pipe(
        catchError((error) => {
          console.error('Error adding volume:', error);
          return throwError(() => error);
        })
      );
  }
  updatedVolume(volumeId: number, volume: Volume): Observable<Volume> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`, // Ajouter le token JWT
    });

    return this.httpClient
      .put<Volume>(`${this.apiUrl}/volumes/${volumeId}`, volume, { headers })
      .pipe(
        catchError((error) => {
          console.error('Error updating volume:', error);
          return throwError(() => error);
        })
      );
  }
  deleteVolume(volumeId: number): Observable<void> {
    const token = this.storageService.getToken(); // Récupérer le token JWT

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`, // Ajouter le token JWT
    });

    return this.httpClient
      .delete<void>(`${this.apiUrl}/volumes/${volumeId}`, { headers })
      .pipe(
        catchError((error: HttpErrorResponse) => {
          console.error('Erreur lors de la suppression du volume:', error);
          return throwError(
            () =>
              new Error(
                'Impossible de supprimer le volume. Veuillez réessayer.'
              )
          );
        })
      );
  }
}
