import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { DeliveryMethod } from '../components/models/DeliveryMethod.model';
import { PickupPointDetails } from '../components/models/pickupPointDetails.model';
import { CartWithDelivery } from '../components/models/CartWithDelivery.model';
import { AddDeliveryMethodRequest } from '../components/models/AddDeliveryMethodRequest.model';
import { DeliveryName } from '../components/models/DeliveryName.enum';
import { DeliveryType } from '../components/models/DeliveryType.model';
import { UpdateDeliveryMethodRequest } from '../components/models/UpdateDeliveryMethodRequest.model';

@Injectable({
  providedIn: 'root',
})
export class DeliveryMethodService {
  private readonly apiUrl = `${environment.apiUrl}/deliveryMethods`;

  constructor(private httpClient: HttpClient) {}

  /**
   * Récupère les options de livraison disponibles pour un utilisateur donné.
   */
  getDeliveryTypes(userId: number): Observable<DeliveryType[]> {
    return this.httpClient
      .get<DeliveryType[]>(`${this.apiUrl}/options/${userId}`)
      .pipe(
        tap((response) => console.log('Types de livraison reçus:', response))
      );
  }
  /**
   * Récupère les modes de livraison disponibles pour un utilisateur donné.
   */
  getUserDeliveryMethods(userId: number): Observable<DeliveryMethod[]> {
    return this.httpClient
      .get<DeliveryMethod[]>(`${this.apiUrl}/methods/${userId}`)
      .pipe(tap((response) => console.log('Réponse du backend:', response)));
  }

  /**
   * Récupère les points relais disponibles pour un utilisateur donné.
   */
  getPickupMethods(userId: number): Observable<PickupPointDetails[]> {
    return this.httpClient
      .get<PickupPointDetails[]>(`${this.apiUrl}/pickupPoints/${userId}`)
      .pipe(
        tap(() => console.log('Points relais récupérés')),
        catchError((error) =>
          this.handleError('récupération des points relais', error)
        )
      );
  }

  /**
   * Récupère le montant total du panier de l'utilisateur.
   */
  getCartTotal(userId: number): Observable<number> {
    return this.httpClient
      .get<number>(`${this.apiUrl}/cart-total/${userId}`)
      .pipe(
        tap(() => console.log('Montant total du panier récupéré')),
        catchError((error) =>
          this.handleError('récupération du montant total du panier', error)
        )
      );
  }

  /**
   * Calcule le total du panier en prenant en compte le coût de la livraison.
   */
  getCartWithDelivery(
    userId: number,
    selectedType: DeliveryName
  ): Observable<CartWithDelivery> {
    const url = `${this.apiUrl}/cart-with-delivery/${userId}?selectedType=${selectedType}`;
    console.log(`Appel API : ${url}`);

    return this.httpClient.get<CartWithDelivery>(url).pipe(
      tap((response) =>
        console.log('Réponse API getCartWithDelivery:', response)
      ),
      catchError((error) =>
        this.handleError('récupération du panier avec livraison', error)
      )
    );
  }

  /**
   * Ajoute une méthode de livraison pour un utilisateur.
   */
  addDeliveryMethod(
    request: AddDeliveryMethodRequest
  ): Observable<DeliveryMethod> {
    return this.httpClient
      .post<DeliveryMethod>(`${this.apiUrl}/add`, request)
      .pipe(
        tap(() => console.log('Méthode de livraison ajoutée')),
        catchError((error) =>
          this.handleError('ajout d’une méthode de livraison', error)
        )
      );
  }

  /**
   * Met à jour une méthode de livraison existante.
   */
  updateDeliveryMethod(
    deliveryMethodId: number,
    request: UpdateDeliveryMethodRequest
  ): Observable<DeliveryMethod> {
    return this.httpClient
      .put<DeliveryMethod>(`${this.apiUrl}/update/${deliveryMethodId}`, request)
      .pipe(
        tap(() => console.log('Méthode de livraison modifiée')),
        catchError((error) =>
          this.handleError('modification d’une méthode de livraison', error)
        )
      );
  }

  /**
   * Gère les erreurs des requêtes HTTP.
   */
  private handleError(operation: string, error: any): Observable<never> {
    let errorMessage = `Une erreur est survenue lors de ${operation}. Veuillez réessayer plus tard.`;

    if (error?.error?.message) {
      errorMessage = `Erreur lors de ${operation} : ${error.error.message}`;
    } else if (error.status) {
      errorMessage = `Erreur ${error.status} lors de ${operation} : ${error.statusText} - URL: ${error.url}`;
    }

    console.error(`[${operation}] ${errorMessage}`, error);
    return throwError(() => new Error(errorMessage));
  }
}
