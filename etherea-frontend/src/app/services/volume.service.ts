import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Volume } from '../components/models/volume.model';

@Injectable({
  providedIn: 'root',
})
export class VolumeService {
  apiUrl = environment.apiUrl;

  constructor(private httpClient: HttpClient) {}

  getAllVolumes(
    page: number,
    size: number
  ): Observable<{
    content: Volume[];
    totalElements: number;
    totalPages: number;
  }> {
    return this.httpClient
      .get<{
        content: Volume[];
        totalElements: number;
        totalPages: number;
      }>(`${this.apiUrl}/volumes?page=${page}&size=${size}`)
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
    return this.httpClient
      .post<Volume>(`${this.apiUrl}/volumes/add`, volume)
      .pipe(
        catchError((error) => {
          console.error('Erreur lors de l’ajout du volume:', error);
          return throwError(() => error);
        })
      );
  }

  updatedVolume(volumeId: number, volume: Volume): Observable<Volume> {
    return this.httpClient
      .put<Volume>(`${this.apiUrl}/volumes/${volumeId}`, volume)
      .pipe(
        catchError((error) => {
          console.error('Erreur lors de la mise à jour du volume:', error);
          return throwError(() => error);
        })
      );
  }

  deleteVolume(volumeId: number): Observable<void> {
    return this.httpClient
      .delete<void>(`${this.apiUrl}/volumes/${volumeId}`)
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
