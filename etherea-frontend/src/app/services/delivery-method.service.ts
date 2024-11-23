import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { DeliveryMethod } from '../components/models/DeliveryMethod.model';
import { Observable } from 'rxjs';
import { PickupPoint } from '../components/models/pickupPoint.model';

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
}
