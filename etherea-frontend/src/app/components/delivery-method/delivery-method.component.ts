import { Component, inject, OnInit, DestroyRef } from '@angular/core';
import { DeliveryAddress } from '../models/DeliveryAddress.model';
import { ActivatedRoute, Router } from '@angular/router';
import { switchMap, filter } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DeliveryMethodService } from 'src/app/services/delivery-method.service';
import { DeliveryMethod } from '../models/DeliveryMethod.model';
import { DeliveryOption } from '../models/DeliveryOption.enum';
import { AuthService } from 'src/app/services/auth.service';

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
  isLoading: boolean = true; // Indicateur de chargement
  deliveryMethods: DeliveryMethod[] = [];
  selectedDeliveryOption!: DeliveryOption;

  private destroyRef = inject(DestroyRef);

  constructor(
    private authService: AuthService,
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
              this.userId = user!.id; // Récupération dynamique de l'ID utilisateur
              return this.deliveryMethodService.getDeliveryMethods(this.userId);
            })
          );
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (methods) => {
          this.deliveryMethods = methods;
          this.isLoading = false; // Fin du chargement
        },
        error: (error) => {
          console.error(
            'Erreur lors de la récupération des méthodes de livraison :',
            error
          );
          this.isLoading = false;
        },
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
