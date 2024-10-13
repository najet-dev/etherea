import { Component, inject } from '@angular/core';
import { DeliveryAddress } from '../models/DeliveryAddress.model';
import { OrderService } from 'src/app/services/order.service';
import { ActivatedRoute } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-delivery-method',
  templateUrl: './delivery-method.component.html',
  styleUrls: ['./delivery-method.component.css'],
})
export class DeliveryMethodComponent {
  deliveryAddress: DeliveryAddress | null = null;

  // Utilisation de inject() au lieu de passer par le constructeur
  private orderService = inject(OrderService);
  private route = inject(ActivatedRoute);

  constructor() {
    // Récupérer les paramètres directement à l'aide de ActivatedRoute
    const userId = Number(this.route.snapshot.paramMap.get('userId'));
    const addressId = Number(this.route.snapshot.paramMap.get('addressId'));

    if (userId && addressId) {
      this.orderService
        .getDeliveryAddress(userId, addressId)
        .pipe(takeUntilDestroyed()) // Désabonnement automatique
        .subscribe(
          (address) => {
            this.deliveryAddress = address;
          },
          (error) => {
            console.error('Error retrieving delivery address:', error);
          }
        );
    }
  }
}
