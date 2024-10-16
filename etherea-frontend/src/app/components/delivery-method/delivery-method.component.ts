import { Component, OnInit } from '@angular/core';
import { DeliveryAddress } from '../models/DeliveryAddress.model';
import { OrderService } from 'src/app/services/order.service';
import { AuthService } from 'src/app/services/auth.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-delivery-method',
  templateUrl: './delivery-method.component.html',
  styleUrls: ['./delivery-method.component.css'],
})
export class DeliveryMethodComponent implements OnInit {
  deliveryAddress: DeliveryAddress | null = null;
  userId: number | null = null;
  addressId: number | null = null;

  constructor(
    private orderService: OrderService,
    private authService: AuthService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      const addressIdParam = params.get('addressId');
      this.addressId = addressIdParam ? +addressIdParam : null;

      if (this.addressId) {
        this.loadCurrentUser(); // Charger l'utilisateur et l'adresse de livraison
      } else {
        console.error(
          "Le paramètre 'addressId' est manquant ou invalide dans l'URL"
        );
      }
    });
  }

  // Charger l'utilisateur actuellement connecté
  loadCurrentUser(): void {
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        this.userId = user ? user.id : null;
        if (this.userId && this.addressId) {
          this.loadDeliveryAddress(); // Charger l'adresse de livraison
        } else {
          console.error("Impossible de récupérer l'utilisateur ou l'adresse.");
        }
      },
      error: (error) => {
        console.error(
          'Erreur lors de la récupération de l’utilisateur :',
          error
        );
      },
    });
  }

  // Récupérer l'adresse de livraison depuis le backend
  loadDeliveryAddress(): void {
    if (this.userId && this.addressId) {
      this.orderService
        .getDeliveryAddress(this.userId, this.addressId)
        .subscribe({
          next: (address) => {
            this.deliveryAddress = address;
            console.log('Adresse de livraison récupérée :', address);
          },
          error: (error) => {
            console.error(
              'Erreur lors de la récupération de l’adresse de livraison :',
              error
            );
          },
        });
    }
  }
}
