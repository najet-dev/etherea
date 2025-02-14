import { Component, EventEmitter, Output, OnInit, Input } from '@angular/core';
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
  userLastName: string = '';
  userFirstName: string = '';
  isPaymentLoading = false;
  errorMessage: string | null = null;
  clientSecret: string | null = null;
  transactionId: string | null = null;
  showSuccessMessage = false; // Contrôle l'affichage du message

  constructor(private appFacade: AppFacade, private cartService: CartService) {}

  async ngOnInit() {
    console.log('PaymentComponent initialisé.');
    try {
      // Récupérer l'ID de l'utilisateur
      const userId: number | null = await firstValueFrom(
        this.appFacade.getCurrentUser()
      );

      if (!userId) {
        throw new Error('Utilisateur non authentifié.');
      }

      const user = await firstValueFrom(this.appFacade.getUserDetails(userId));

      if (user && user.lastName && user.firstName) {
        this.userLastName = user.lastName;
        this.userFirstName = user.firstName;
      }
    } catch (error) {
      console.error(
        "Erreur lors de la récupération du nom de l'utilisateur :",
        error
      );
    }
  }

  async onPaymentMethodChange(method: string) {
    this.selectedPaymentMethod = method;
    this.paymentSelected.emit(method);

    if (method === 'CREDIT_CARD') {
      console.log('Passage au paiement par carte bancaire.');

      if (!this.stripe) {
        this.stripe = await loadStripe(environment.stripePublicKey);
      }

      if (this.stripe) {
        // Détruit les anciens éléments avant de recréer les nouveaux
        if (this.cardNumberElement) {
          this.cardNumberElement.unmount();
          this.cardNumberElement = null;
        }
        if (this.cardExpiryElement) {
          this.cardExpiryElement.unmount();
          this.cardExpiryElement = null;
        }
        if (this.cardCvcElement) {
          this.cardCvcElement.unmount();
          this.cardCvcElement = null;
        }

        this.elements = this.stripe.elements();

        // Re-création des champs Stripe Elements
        console.log('Re-création des éléments Stripe...');
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

      // Vérifie si une transaction existe déjà pour éviter de recréer une intention de paiement
      if (this.clientSecret && this.transactionId) {
        return;
      }

      try {
        const userId: number | null = await firstValueFrom(
          this.appFacade.getCurrentUser()
        );

        if (!userId) {
          throw new Error('Utilisateur non authentifié.');
        }

        const cartId = await firstValueFrom(this.appFacade.getCartId(userId));

        if (!cartId) {
          throw new Error('Le panier est introuvable.');
        }

        console.log("Création d'une nouvelle intention de paiement...");
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
        console.error('Erreur lors de la récupération du paiement :', error);
        this.errorMessage = 'Échec de la création du paiement.';
      }
    }
  }

  async submitPayment() {
    if (!this.stripe || !this.elements || !this.cardNumberElement) return;

    this.isPaymentLoading = true;
    this.errorMessage = null;
    this.showSuccessMessage = false; // Réinitialiser le message de succès avant de commencer

    try {
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

      // Afficher le message de succès
      this.showSuccessMessage = true;

      // Masquer le message après 5 secondes
      setTimeout(() => {
        this.showSuccessMessage = false;
      }, 5000);

      // Réinitialisation du formulaire après paiement réussi
      this.cardholderName = '';
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
