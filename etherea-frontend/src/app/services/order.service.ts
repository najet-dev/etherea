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
  private deliveryAddressSubject = new BehaviorSubject<DeliveryAddress[]>([]); // Modifié pour stocker les adresses de livraison
  deliveryAddress$ = this.deliveryAddressSubject.asObservable();
  userId: number | null = null;

  constructor(
    private httpClient: HttpClient,
    private authService: AuthService
  ) {}

  /**
   * Ajoute une nouvelle adresse de livraison pour un utilisateur donné.
   * @param userId L'ID de l'utilisateur.
   * @param deliveryAddress L'objet DeliveryAddress à envoyer au backend.
   */
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
          // Met à jour le BehaviorSubject avec la nouvelle adresse ajoutée
          const currentAddresses = this.deliveryAddressSubject.value;
          this.deliveryAddressSubject.next([...currentAddresses, newAddress]);
        }),
        catchError((error: HttpErrorResponse) => {
          console.error('Error adding delivery address:', error);
          return throwError(() => error);
        })
      );
  }
}
