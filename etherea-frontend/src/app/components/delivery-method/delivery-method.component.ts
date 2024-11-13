import { Component, inject, OnInit, DestroyRef, Input } from '@angular/core';
import { DeliveryAddress } from '../models/DeliveryAddress.model';
import { OrderService } from 'src/app/services/order.service';
import { AuthService } from 'src/app/services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { switchMap, filter } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DeliveryAddressService } from 'src/app/services/delivery-address.service';
import { AppFacade } from 'src/app/services/appFacade.service';
import { DeliveryMethodService } from 'src/app/services/delivery-method.service';
import { DeliveryMethod } from '../models/DeliveryMethod.model';
import { DeliveryOption } from '../models/DeliveryOption.enum';

@Component({
  selector: 'app-delivery-method',
  templateUrl: './delivery-method.component.html',
  styleUrls: ['./delivery-method.component.css'],
})
export class DeliveryMethodComponent implements OnInit {
  deliveryAddress: DeliveryAddress | null = null;
  userId: number | null = null;
  addressId: number | null = null;
  firstName: string | null = null;
  lastName: string | null = null;
  isLoading: boolean = true; // Ajout d'un indicateur de chargement
  deliveryMethods: DeliveryMethod[] = [];
  selectedDeliveryOption!: DeliveryOption;

  private destroyRef = inject(DestroyRef);

  constructor(
    private authService: AuthService,
    private appFacade: AppFacade,
    private route: ActivatedRoute,
    private router: Router,
    private deliveryMethodService: DeliveryMethodService
  ) {}

  ngOnInit(): void {
    this.route.paramMap
      .pipe(
        filter((params) => !!params.get('addressId')),
        switchMap((params) => {
          const addressId = +params.get('addressId')!;
          this.addressId = addressId;
          return this.authService.getCurrentUser().pipe(
            filter((user) => user !== null && user.id !== undefined),
            switchMap((user) => {
              this.userId = user!.id;
              return this.appFacade.getDeliveryAddress(this.userId, addressId);
            })
          );
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (address) => {
          this.deliveryAddress = address;
          this.isLoading = false; // Fin du chargement

          if (address.user && address.user.firstName && address.user.lastName) {
            this.firstName = address.user.firstName;
            this.lastName = address.user.lastName;
          }
        },
        error: (error) => {
          console.error(
            'Erreur lors de la récupération de l’adresse de livraison :',
            error
          );
          this.isLoading = false;
        },
      });
    const userId = 2;
    this.deliveryMethodService.getDeliveryMethods(userId).subscribe((data) => {
      this.deliveryMethods = data;
    });
  }

  onEditAddress(): void {
    if (this.addressId) {
      this.router.navigate(['/order', this.addressId]);
    } else {
      console.error("L'ID de l'adresse n'est pas défini.");
    }
  }
  onDeliveryOptionChange(option: DeliveryOption): void {
    this.selectedDeliveryOption = option;
  }
}
