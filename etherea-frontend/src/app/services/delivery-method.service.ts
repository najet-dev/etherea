import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { DeliveryMethod } from '../components/models/DeliveryMethod.model';
import { PickupPoint } from '../components/models/pickupPoint.model';
import { CartWithDelivery } from '../components/models/CartWithDelivery.model';
import { DeliveryMethodDTO } from '../components/models/DeliveryMethodDTO.model';
import { AddDeliveryMethodRequest } from '../components/models/AddDeliveryMethodRequest .model';
import { DeliveryType } from '../components/models/DeliveryType.enum';

@Injectable({
  providedIn: 'root',
})
export class DeliveryMethodService {
  private apiUrl = `${environment.apiUrl}/deliveryMethods`;

  constructor(private httpClient: HttpClient) {}

  // Récupérer les options de livraison disponibles pour l'utilisateur
  getDeliveryMethods(userId: number): Observable<DeliveryMethod[]> {
    return this.httpClient
      .get<DeliveryMethod[]>(`${this.apiUrl}/options/${userId}`)
      .pipe(
        tap(() => console.log('Options de livraison récupérées')),
        catchError(this.handleError('récupération des méthodes de livraison'))
      );
  }

  // Récupérer les points relais disponibles pour l'utilisateur
  getPickupMethods(userId: number): Observable<PickupPoint[]> {
    return this.httpClient
      .get<PickupPoint[]>(`${this.apiUrl}/pickupPoints/${userId}`)
      .pipe(
        tap(() => console.log('Points relais récupérés')),
        catchError(this.handleError('récupération des points relais'))
      );
  }

  // Récupérer le panier avec la méthode de livraison sélectionnée
  getCartWithDelivery(
    userId: number,
    selectedOption: DeliveryType
  ): Observable<CartWithDelivery> {
    return this.httpClient
      .get<CartWithDelivery>(
        `${this.apiUrl}/cart-with-delivery/${userId}?selectedOption=${selectedOption}`
      )
      .pipe(
        tap(() => console.log('Panier avec livraison récupéré')),
        catchError(this.handleError('récupération du panier avec livraison'))
      );
  }

  // Récupérer le montant total du panier
  getCartTotal(userId: number): Observable<number> {
    return this.httpClient
      .get<number>(`${this.apiUrl}/cart-total/${userId}`)
      .pipe(
        tap(() => console.log('Montant total du panier récupéré')),
        catchError(this.handleError('récupération du montant total du panier'))
      );
  }

  // Ajouter une méthode de livraison
  addDeliveryMethod(
    request: AddDeliveryMethodRequest
  ): Observable<DeliveryMethodDTO> {
    return this.httpClient
      .post<DeliveryMethodDTO>(`${this.apiUrl}/add`, request)
      .pipe(
        tap(() => console.log('Méthode de livraison ajoutée')),
        catchError(this.handleError('ajout d’une méthode de livraison'))
      );
  }

  // Gestion des erreurs
  private handleError(operation: string) {
    return (error: any) => {
      console.error(`Erreur lors de ${operation}:`, error);
      return throwError(
        () =>
          new Error(
            `Une erreur est survenue lors de ${operation}. Veuillez réessayer plus tard.`
          )
      );
    };
  }
}
