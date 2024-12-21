import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { DeliveryMethod } from '../components/models/DeliveryMethod.model';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { PickupPoint } from '../components/models/pickupPoint.model';
import { CartWithDelivery } from '../components/models/CartWithDelivery.model';
import { DeliveryMethodDTO } from '../components/models/DeliveryMethodDTO.model';
import { AddDeliveryMethodRequestDTO } from '../components/models/AddDeliveryMethodRequestDTO .model';

@Injectable({
  providedIn: 'root',
})
export class DeliveryMethodService {
  apiUrl = environment.apiUrl;

  constructor(private httpClient: HttpClient) {}

  getDeliveryMethods(userId: number): Observable<DeliveryMethod[]> {
    return this.httpClient.get<DeliveryMethod[]>(
      `${this.apiUrl}/deliveryMethods/options/${userId}`
    );
  }
  getPickupMethods(userId: number): Observable<PickupPoint[]> {
    return this.httpClient.get<PickupPoint[]>(
      `${this.apiUrl}/deliveryMethods/pickupPoints/${userId}`
    );
  }
  getCartWithDelivery(
    userId: number,
    selectedOption: string
  ): Observable<CartWithDelivery> {
    return this.httpClient.get<CartWithDelivery>(
      `${this.apiUrl}/deliveryMethods/cart-with-delivery/${userId}?selectedOption=${selectedOption}`
    );
  }
  getCartTotal(userId: number): Observable<number> {
    return this.httpClient.get<number>(
      `${this.apiUrl}/deliveryMethods/cart-total/${userId}`
    );
  }

  // addDeliveryMethod(
  //   selectedMethod: DeliveryMethod
  // ): Observable<DeliveryMethod> {
  //   return this.httpClient
  //     .post<DeliveryMethod>(
  //       `${this.apiUrl}/deliveryMethods/add/`,
  //       selectedMethod
  //     )
  //     .pipe(
  //       tap((response) => {
  //         console.log('Méthode de livraison ajoutée avec succès:', response);
  //       }),
  //       catchError((error) => {
  //         console.error(
  //           "Erreur lors de l'ajout de la méthode de livraison:",
  //           error
  //         );
  //         return throwError(() => error);
  //       })
  //     );
  // }
  addDeliveryMethod(
    request: AddDeliveryMethodRequestDTO
  ): Observable<DeliveryMethodDTO> {
    return this.httpClient.post<DeliveryMethodDTO>(
      `${environment.apiUrl}/deliveryMethods/add`,
      request
    );
  }
}
