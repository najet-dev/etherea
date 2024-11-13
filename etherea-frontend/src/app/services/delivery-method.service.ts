import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { DeliveryMethod } from '../components/models/DeliveryMethod.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class DeliveryMethodService {
  apiUrl = environment.apiUrl;

  constructor(private httpClient: HttpClient) {}

  getDeliveryMethods(userId: number): Observable<DeliveryMethod[]> {
    return this.httpClient.get<DeliveryMethod[]>(
      `${this.apiUrl}/deliveryMethods/${userId}`
    );
  }
}
