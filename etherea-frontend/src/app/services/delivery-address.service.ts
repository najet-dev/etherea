import { Injectable } from '@angular/core';
import {
  BehaviorSubject,
  catchError,
  Observable,
  switchMap,
  tap,
  throwError,
} from 'rxjs';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { DeliveryAddress } from '../components/models/deliveryAddress.model';

@Injectable({
  providedIn: 'root',
})
export class DeliveryAddressService {
  apiUrl = environment.apiUrl;
  private deliveryAddressSubject = new BehaviorSubject<DeliveryAddress[]>([]);
  deliveryAddress$ = this.deliveryAddressSubject.asObservable();
  private defaultAddressSubject = new BehaviorSubject<DeliveryAddress | null>(
    null
  );
  defaultAddress$ = this.defaultAddressSubject.asObservable();

  constructor(private httpClient: HttpClient) {}

  // Méthode pour récupérer toutes les adresses de livraison d'un utilisateur
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

  // Méthode pour récupérer une adresse spécifique d'un utilisateur
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
        catchError((error) => {
          console.error("Erreur lors de la récupération de l'adresse :", error);
          return throwError(
            () => new Error("Impossible de récupérer l'adresse.")
          );
        })
      );
  }

  // Méthode pour ajouter une nouvelle adresse de livraison
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
        catchError((error) => {
          console.error(
            "Erreur lors de l'ajout de l'adresse de livraison:",
            error
          );
          return throwError(() => error);
        })
      );
  }

  // Méthode pour mettre à jour une adresse de livraison existante
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
        catchError((error) => {
          console.error(
            "Erreur lors de la mise à jour de l'adresse de livraison:",
            error
          );
          return throwError(() => error);
        })
      );
  }
  setDefaultAddress(userId: number, addressId: number) {
    return this.httpClient
      .put(
        `${this.apiUrl}/deliveryAddresses/${userId}/${addressId}/set-default`,
        {}
      )
      .pipe(
        switchMap(() =>
          this.getDeliveryAddress(userId, addressId).pipe(
            tap((address) => this.defaultAddressSubject.next(address))
          )
        ),
        catchError((error) => {
          console.error(
            "Erreur lors de la définition de l'adresse par défaut :",
            error
          );
          return throwError(
            () => new Error("Impossible de définir l'adresse par défaut.")
          );
        })
      );
  }

  setDefaultAddressState(address: DeliveryAddress) {
    this.defaultAddressSubject.next(address);
  }
  resetAddresses(): void {
    this.deliveryAddressSubject.next([]);
    this.defaultAddressSubject.next(null);
  }
}
