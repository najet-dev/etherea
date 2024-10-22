import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable, tap, throwError } from 'rxjs';
import { DeliveryAddress } from '../components/models/DeliveryAddress.model';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { AuthService } from './auth.service';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  apiUrl = environment.apiUrl;
  private deliveryAddressSubject = new BehaviorSubject<DeliveryAddress[]>([]);
  deliveryAddress$ = this.deliveryAddressSubject.asObservable();

  constructor(
    private httpClient: HttpClient,
    private authService: AuthService
  ) {}

  getUserDeliveryAddresses(userId: number): Observable<DeliveryAddress[]> {
    return this.httpClient
      .get<DeliveryAddress[]>(`${this.apiUrl}/deliveryAddresses/${userId}`)
      .pipe(
        catchError((error) => {
          console.error(
            'Erreur lors de la récupération des adresses de livraison:',
            error
          );
          return throwError(
            () => new Error('Erreur lors de la récupération des adresses.')
          );
        })
      );
  }

  getDeliveryAddress(
    userId: number,
    addressId: number
  ): Observable<DeliveryAddress> {
    return this.httpClient
      .get<DeliveryAddress>(
        `${this.apiUrl}/deliveryAddresses/${userId}/${addressId}`
      )
      .pipe(
        tap((address) => {
          console.log('Adresse de livraison récupérée:', address);
        }),
        catchError(this.handleError)
      );
  }

  addDeliveryAddress(
    userId: number,
    deliveryAddress: DeliveryAddress
  ): Observable<DeliveryAddress> {
    return this.httpClient
      .post<DeliveryAddress>(
        `${this.apiUrl}/deliveryAddresses/${userId}`,
        deliveryAddress
      )
      .pipe(
        tap((newAddress) => {
          console.log('Nouvelle adresse ajoutée avec succès:', newAddress);
        }),
        catchError(this.handleError)
      );
  }

  updateDeliveryAddress(
    userId: number,
    deliveryAddress: DeliveryAddress
  ): Observable<DeliveryAddress> {
    return this.httpClient
      .put<DeliveryAddress>(
        `${this.apiUrl}/deliveryAddresses/${userId}`,
        deliveryAddress
      )
      .pipe(
        tap((updatedAddress) => {
          console.log('Adresse modifiée avec succès:', updatedAddress);
        }),
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage: string;
    if (error.error instanceof ErrorEvent) {
      // Une erreur côté client ou un problème de réseau
      errorMessage = `Erreur: ${error.error.message}`;
    } else {
      // L'erreur est retournée par le backend
      errorMessage = `Code d'erreur: ${error.status}, Message: ${error.message}`;
    }
    console.error('Une erreur est survenue:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}
