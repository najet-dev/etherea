import { Component, EventEmitter, Output, OnInit, Input } from '@angular/core';
import { PaymentService } from '../../services/payment.service';
import { CartService } from '../../services/cart.service';
import { PaymentOption } from '../models/PaymentOption.enum';

import {
  loadStripe,
  Stripe,
  StripeElements,
  StripeCardNumberElement,
  StripeCardExpiryElement,
  StripeCardCvcElement,
} from '@stripe/stripe-js';
import { environment } from 'src/environments/environment';
import { UserService } from 'src/app/services/user.service';
import { firstValueFrom } from 'rxjs';
import { AppFacade } from 'src/app/services/appFacade.service';

@Component({
  selector: 'app-payment',
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.css'],
})
export class PaymentComponent implements OnInit {
  @Output() paymentSelected = new EventEmitter<string>();

  selectedPaymentMethod: string | null = null;
  stripe: Stripe | null = null;
  elements: StripeElements | null = null;
  cardNumberElement: StripeCardNumberElement | null = null;
  cardExpiryElement: StripeCardExpiryElement | null = null;
  cardCvcElement: StripeCardCvcElement | null = null;
  cardholderName: string = '';
  isPaymentLoading = false;
  errorMessage: string | null = null;
  clientSecret: string | null = null;
  transactionId: string | null = null;

  constructor(private appFacade: AppFacade, private cartService: CartService) {}

  async ngOnInit() {
    console.log('PaymentComponent initialisé.');
  }

  async onPaymentMethodChange(method: string) {
    this.selectedPaymentMethod = method;
    this.paymentSelected.emit(method);

    if (method === 'CREDIT_CARD') {
      console.log('Initialisation de Stripe...');

      if (!this.stripe) {
        this.stripe = await loadStripe(environment.stripePublicKey);
      }

      if (this.stripe && !this.elements) {
        this.elements = this.stripe.elements();
        this.cardNumberElement = this.elements.create('cardNumber', {
          style: { base: { fontSize: '16px' } },
        });
        this.cardExpiryElement = this.elements.create('cardExpiry', {
          style: { base: { fontSize: '16px' } },
        });
        this.cardCvcElement = this.elements.create('cardCvc', {
          style: { base: { fontSize: '16px' } },
        });

        setTimeout(() => {
          this.cardNumberElement?.mount('#card-number');
          this.cardExpiryElement?.mount('#card-expiry');
          this.cardCvcElement?.mount('#card-cvc');
        }, 0);
      }

      try {
        // Obtenir l'utilisateur connecté
        const userId: number | null = await firstValueFrom(
          this.appFacade.getCurrentUser()
        );

        console.log('ID utilisateur récupéré :', userId);

        if (!userId) {
          throw new Error('Utilisateur non authentifié.');
        }

        // Récupérer l'ID du panier de l'utilisateur
        const cartId = await firstValueFrom(this.appFacade.getCartId(userId));

        if (!cartId) {
          throw new Error('Le panier est introuvable.');
        }

        console.log('ID du panier récupéré :', cartId);

        // Création de l'intention de paiement
        const paymentResponse = await firstValueFrom(
          this.appFacade.createPayment({
            paymentOption: PaymentOption.CREDIT_CARD,
            cartId: cartId,
          })
        );

        if (!paymentResponse || !paymentResponse.clientSecret) {
          throw new Error('Client secret non reçu.');
        }

        this.clientSecret = paymentResponse.clientSecret;
        this.transactionId = paymentResponse.transactionId;
        console.log('Transaction ID reçu :', this.transactionId);
      } catch (error) {
        console.error(
          'Erreur lors de la récupération du panier ou du paiement :',
          error
        );
        this.errorMessage = 'Échec de la création du paiement.';
      }
    }
  }

  async submitPayment() {
    if (!this.stripe || !this.elements || !this.cardNumberElement) return;

    this.isPaymentLoading = true;
    this.errorMessage = null;

    try {
      console.log('Création du moyen de paiement...');
      const { paymentMethod, error } = await this.stripe.createPaymentMethod({
        type: 'card',
        card: this.cardNumberElement,
        billing_details: { name: this.cardholderName },
      });

      if (error || !paymentMethod) {
        this.errorMessage = error?.message || 'Erreur lors du paiement.';
        this.isPaymentLoading = false;
        return;
      }

      console.log('Confirmation du paiement...');
      if (!this.transactionId) {
        this.errorMessage = "Erreur : l'ID de la transaction est invalide.";
        this.isPaymentLoading = false;
        return;
      }

      const confirmationResponse = await firstValueFrom(
        this.appFacade.confirmPayment(this.transactionId, paymentMethod.id)
      );

      if (confirmationResponse?.paymentStatus !== 'SUCCESS') {
        throw new Error('Le paiement a échoué.');
      }

      alert('Paiement réussi !');

      // Réinitialisation du formulaire après paiement réussi
      this.cardholderName = ''; // Réinitialise le champ du nom
      this.selectedPaymentMethod = null; // Désélectionne la méthode de paiement

      // Vide les champs Stripe
      this.cardNumberElement.clear();
      this.cardExpiryElement?.clear();
      this.cardCvcElement?.clear();

      // Rafraîchir les données du panier
      const userId: number | null = await firstValueFrom(
        this.appFacade.getCurrentUser()
      );
      if (userId) {
        this.cartService.refreshCart(userId); // fonction pour rafraîchir le panier
      }
    } catch (error: any) {
      this.errorMessage = error.message || 'Erreur de paiement.';
    } finally {
      this.isPaymentLoading = false;
    }
  }
}
