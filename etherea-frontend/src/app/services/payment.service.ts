import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { loadStripe, Stripe } from '@stripe/stripe-js';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { PaymentRequest } from '../components/models/PaymentRequest.model';

@Injectable({
  providedIn: 'root',
})
export class PaymentService {
  private apiUrl = `${environment.apiUrl}`; // URL de l'API backend
  private stripePromise = loadStripe(environment.stripePublicKey); // Charge Stripe avec la clé publique

  constructor(private httpClient: HttpClient) {}

  /**
   * Retourne une instance de Stripe (charge Stripe si nécessaire).
   * @returns Promise<Stripe | null>
   */
  async getStripeInstance(): Promise<Stripe | null> {
    return this.stripePromise;
  }

  /**
   * Crée une intention de paiement en communiquant avec le backend.
   * @param paymentRequest Les données nécessaires pour créer l'intention de paiement.
   * @returns Observable contenant le clientSecret renvoyé par Stripe via le backend.
   */
  createPayment(
    paymentRequest: PaymentRequest
  ): Observable<{ clientSecret: string }> {
    return this.httpClient
      .post<{ clientSecret: string }>(
        `${this.apiUrl}/payments/createPayment`,
        paymentRequest
      )
      .pipe(
        catchError((error) => {
          console.error('Erreur lors de la création du paiement', error);
          return throwError(
            () =>
              new Error(
                'Une erreur est survenue lors de la création du paiement.'
              )
          );
        })
      );
  }
  /**
   * Confirme un paiement après la validation par l'utilisateur.
   * @param transactionId L'identifiant unique de la transaction.
   * @returns Observable contenant le PaymentResponse mis à jour.
   */
  confirmPayment(transactionId: string): Observable<PaymentResponse> {
    return this.httpClient
      .post<PaymentResponse>(`${this.apiUrl}/payments/confirmPayment`, {
        transactionId,
      })
      .pipe(
        catchError((error) => {
          console.error('Erreur lors de la confirmation du paiement', error);
          return throwError(
            () =>
              new Error(
                'Une erreur est survenue lors de la confirmation du paiement.'
              )
          );
        })
      );
  }
}
