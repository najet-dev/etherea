import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable, tap, throwError } from 'rxjs';
import { DeliveryAddress } from '../components/models/deliveryAddress.model';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class DeliveryAddressService {
  apiUrl = environment.apiUrl;
  private deliveryAddressSubject = new BehaviorSubject<DeliveryAddress[]>([]);
  deliveryAddress$ = this.deliveryAddressSubject.asObservable();

  constructor(private httpClient: HttpClient) {}

  /**
   * Récupère toutes les adresses de livraison d'un utilisateur.
   */
  getUserDeliveryAddresses(userId: number): Observable<DeliveryAddress[]> {
    return this.httpClient
      .get<DeliveryAddress[]>(`${this.apiUrl}/deliveryAddresses/${userId}`)
      .pipe(
        tap((addresses) => this.deliveryAddressSubject.next(addresses)),
        catchError(
          this.handleError('Erreur lors de la récupération des adresses.')
        )
      );
  }

  /**
   * Récupère une adresse de livraison spécifique.
   */
  getDeliveryAddress(
    userId: number,
    addressId: number
  ): Observable<DeliveryAddress> {
    return this.httpClient
      .get<DeliveryAddress>(
        `${this.apiUrl}/deliveryAddresses/${userId}/${addressId}`
      )
      .pipe(
        tap((address) => console.log('Adresse récupérée :', address)),
        catchError(this.handleError('Impossible de récupérer l’adresse.'))
      );
  }

  /**
   * Ajoute une nouvelle adresse de livraison pour un utilisateur.
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
          console.log('Nouvelle adresse ajoutée avec succès:', newAddress);
          this.deliveryAddressSubject.next([
            ...this.deliveryAddressSubject.value,
            newAddress,
          ]);
        }),
        catchError(this.handleError('Erreur lors de l’ajout de l’adresse.'))
      );
  }

  /**
   * Met à jour une adresse de livraison existante.
   */
  updateDeliveryAddress(
    userId: number,
    deliveryAddress: DeliveryAddress
  ): Observable<DeliveryAddress> {
    if (!deliveryAddress.id) {
      return throwError(
        () => new Error('L’ID de l’adresse est requis pour la mise à jour.')
      );
    }

    return this.httpClient
      .put<DeliveryAddress>(
        `${this.apiUrl}/deliveryAddresses/${userId}`,
        deliveryAddress
      )
      .pipe(
        tap((updatedAddress) => {
          console.log('Adresse mise à jour avec succès:', updatedAddress);
          this.refreshAddresses(userId);
        }),
        catchError(
          this.handleError('Erreur lors de la mise à jour de l’adresse.')
        )
      );
  }

  /**
   * Met à jour la liste des adresses après modification.
   */
  private refreshAddresses(userId: number): void {
    this.getUserDeliveryAddresses(userId).subscribe();
  }

  /**
   * Gestion centralisée des erreurs HTTP.
   */
  private handleError(message: string) {
    return (error: HttpErrorResponse) => {
      console.error(`${message}`, error);
      return throwError(() => new Error(`${message} Veuillez réessayer.`));
    };
  }
}
