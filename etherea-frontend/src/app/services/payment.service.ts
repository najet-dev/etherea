import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class PaymentService {
  apiUrl = environment.apiUrl;

  constructor(private httpClient: HttpClient) {}

  addPayment(payment: PaymentRequest): Observable<PaymentResponse> {
    return this.httpClient
      .post<PaymentResponse>(`${this.apiUrl}/payments/process`, payment)
      .pipe(
        catchError((error) => {
          console.error('Erreur lors du traitement du paiement', error);
          return throwError(
            () => new Error('Échec du traitement du paiement.')
          );
        })
      );
  }
}
