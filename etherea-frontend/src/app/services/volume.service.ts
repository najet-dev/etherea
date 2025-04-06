import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { StorageService } from './storage.service';
import { Volume } from '../components/models/volume.model';

@Injectable({
  providedIn: 'root',
})
export class VolumeService {
  apiUrl = environment.apiUrl;

  constructor(
    private httpClient: HttpClient,
    private storageService: StorageService
  ) {}

  getAllVolumes(
    page: number = 0,
    size: number = 5
  ): Observable<{
    content: Volume[];
    totalElements: number;
    totalPages: number;
  }> {
    return this.httpClient
      .get<{ content: Volume[]; totalElements: number; totalPages: number }>(
        `${this.apiUrl}/volumes?page=${page}&size=${size}`
      )
      .pipe(
        tap((response) => console.log('API Response:', response)),
        catchError((error) => {
          console.error('Erreur lors de la récupération des volumes:', error);
          return throwError(
            () => new Error('Impossible de récupérer les volumes.')
          );
        })
      );
  }

  addVolume(volume: Volume): Observable<Volume> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`, // Ajout du token JWT
    });

    return this.httpClient
      .post<Volume>(`${this.apiUrl}/volumes/add`, volume, {
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
      Authorization: `Bearer ${token}`, // Ajout du token JWT
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
      Authorization: `Bearer ${token}`, // Ajout du token JWT
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
