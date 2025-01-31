import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { loadStripe, Stripe } from '@stripe/stripe-js';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { PaymentRequest } from '../components/models/PaymentRequest.model';
import { PaymentResponse } from '../components/models/PaymentResponse.model';

@Injectable({
  providedIn: 'root',
})
export class PaymentService {
  private apiUrl = `${environment.apiUrl}/payments`;
  private stripePromise = loadStripe(environment.stripePublicKey);

  constructor(private httpClient: HttpClient) {}

  /**
   * Charge une instance Stripe si elle n'est pas encore chargée.
   * @returns Promise<Stripe | null>
   */
  async getStripeInstance(): Promise<Stripe | null> {
    return this.stripePromise;
  }

  /**
   * Crée une intention de paiement avec le backend.
   * @param paymentRequest Les informations de paiement.
   * @returns Observable contenant le clientSecret et l'ID de transaction.
   */
  createPayment(paymentRequest: PaymentRequest): Observable<PaymentResponse> {
    return this.httpClient
      .post<PaymentResponse>(`${this.apiUrl}/createPayment`, paymentRequest)
      .pipe(
        catchError((error) => {
          console.error('Erreur lors de la création du paiement :', error);
          return throwError(
            () => new Error('Échec de la création du paiement.')
          );
        })
      );
  }

  /**
   * Confirme un paiement en fournissant l'ID de transaction et l'ID du moyen de paiement.
   * @param paymentIntentId L'ID du PaymentIntent généré par Stripe.
   * @param paymentMethodId L'ID du moyen de paiement sélectionné.
   * @returns Observable avec le statut de paiement mis à jour.
   */
  confirmPayment(
    paymentIntentId: string,
    paymentMethodId: string
  ): Observable<PaymentResponse> {
    return this.httpClient
      .post<PaymentResponse>(`${this.apiUrl}/confirm`, {
        paymentIntentId,
        paymentMethodId,
      })
      .pipe(
        catchError((error) => {
          console.error('Erreur lors de la confirmation du paiement :', error);
          return throwError(
            () => new Error('Échec de la confirmation du paiement.')
          );
        })
      );
  }
}
