import { Component, OnInit } from '@angular/core';
import { DeliveryAddress } from '../models/DeliveryAddress.model';
import { OrderService } from 'src/app/services/order.service';
import { AuthService } from 'src/app/services/auth.service';
import { ActivatedRoute } from '@angular/router';
import { switchMap, filter } from 'rxjs/operators';

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
    this.route.paramMap
      .pipe(
        filter((params) => !!params.get('addressId')),
        switchMap((params) => {
          const addressId = +params.get('addressId')!;
          this.addressId = addressId;
          return this.authService.getCurrentUser().pipe(
            filter((user) => user !== null && user.id !== undefined), // Vérification explicite
            switchMap((user) => {
              this.userId = user!.id; // Utilisation de l'opérateur "!" pour affirmer que user n'est pas null
              return this.orderService.getDeliveryAddress(
                this.userId,
                addressId
              );
            })
          );
        })
      )
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
