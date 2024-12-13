import { Component, inject, OnInit, DestroyRef } from '@angular/core';
import { DeliveryAddress } from '../models/DeliveryAddress.model';
import { OrderService } from 'src/app/services/order.service';
import { AuthService } from 'src/app/services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { switchMap, filter } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AppFacade } from 'src/app/services/appFacade.service';
import { DeliveryMethod } from '../models/DeliveryMethod.model';
import { DeliveryMethodService } from 'src/app/services/delivery-method.service'; // Importer le service
import { PickupPoint } from '../models/pickupPoint.model';
import { Modal } from 'bootstrap';

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
  deliveryMethod: DeliveryMethod[] = [];
  pickupPoints: PickupPoint[] = [];
  selectedPickupPoint: PickupPoint | null = null; // Point relais sélectionné
  selectedDeliveryOption: string | null = null; // Option de livraison sélectionnée
  confirmedPickupPoint: PickupPoint | null = null;
  cartTotal: number | null = null;
  deliveryCost: number | null = null;
  total: number | null = null;

  private destroyRef = inject(DestroyRef);

  constructor(
    private appFacade: AppFacade,
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
          this.isLoading = false; // Fin du chargement de l'adresse

          if (address.user && address.user.firstName && address.user.lastName) {
            this.firstName = address.user.firstName;
            this.lastName = address.user.lastName;
          }

          // Appel pour récupérer les modes de livraison après avoir récupéré l'adresse
          this.loadDeliveryMethods();
        },
        error: (error) => {
          console.error(
            'Erreur lors de la récupération de l’adresse de livraison :',
            error
          );
          this.isLoading = false;
        },
      });
    if (this.selectedDeliveryOption) {
      this.loadCartWithDelivery(this.selectedDeliveryOption);
    }
  }

  // Méthode pour charger les modes de livraison
  loadDeliveryMethods(): void {
    if (this.userId) {
      this.isLoading = true; // Mettre en chargement les modes de livraison
      this.deliveryMethodService
        .getDeliveryMethods(this.userId)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (methods) => {
            this.deliveryMethod = methods;
            this.isLoading = false; // Fin du chargement des méthodes de livraison
          },
          error: (error) => {
            console.error(
              'Erreur lors du chargement des méthodes de livraison :',
              error
            );
            this.isLoading = false;
          },
        });
    } else {
      console.log(
        'User ID non défini, impossible de récupérer les modes de livraison'
      );
    }
  }

  onEditAddress(): void {
    if (this.addressId) {
      this.router.navigate(['/order', this.addressId]);
    } else {
      console.error("L'ID de l'adresse n'est pas défini.");
    }
  }
  showPickupPoints(): void {
    if (this.userId) {
      this.deliveryMethodService
        .getPickupMethods(this.userId)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (points) => (this.pickupPoints = points),
          error: (error) =>
            console.error(
              'Erreur lors de la récupération des points relais:',
              error
            ),
        });
    }
  }

  selectPickupPoint(point: PickupPoint): void {
    this.selectedPickupPoint = point;
    console.log('Point relais sélectionné:', point);
  }

  onDeliveryOptionChange(): void {
    if (this.selectedDeliveryOption) {
      this.isLoading = true;
      this.deliveryMethodService
        .getCartWithDelivery(this.userId!, this.selectedDeliveryOption)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (data) => {
            this.cartTotal = data.cartTotal ?? 0;
            this.deliveryCost = data.deliveryCost ?? 0;
            this.total = data.total ?? this.cartTotal + this.deliveryCost;
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Erreur lors du calcul du total :', error);
            this.isLoading = false;
          },
        });
    } else {
      console.log('Aucune option de livraison sélectionnée.');
    }
  }

  confirmPickupPoint(): void {
    if (this.selectedPickupPoint) {
      console.log('Point relais confirmé:', this.selectedPickupPoint);
      this.confirmedPickupPoint = this.selectedPickupPoint; // Stocker le point relais confirmé

      // Fermer la modale
      const modalElement = document.getElementById('pickupPointModal');
      if (modalElement) {
        const modalInstance =
          Modal.getInstance(modalElement) || new Modal(modalElement);
        modalInstance.hide();
      }

      // Supprimer manuellement la classe "modal-open" du body si elle persiste
      const body = document.body;
      if (body.classList.contains('modal-open')) {
        body.classList.remove('modal-open');
      }

      // Supprimer les divs de fond ajoutées par Bootstrap
      const backdrop = document.querySelector('.modal-backdrop');
      if (backdrop) {
        backdrop.remove();
      }
    }
  }

  loadCartWithDelivery(selectedOption: string): void {
    if (this.userId && selectedOption) {
      this.isLoading = true;
      this.deliveryMethodService
        .getCartWithDelivery(this.userId, selectedOption)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (data) => {
            this.cartTotal = data.cartTotal ?? 0;
            this.deliveryCost = data.deliveryCost ?? 0;
            this.total = data.total ?? this.cartTotal + this.deliveryCost;
            this.isLoading = false;
          },
          error: (error) => {
            console.error(
              'Erreur lors de la récupération des informations du panier :',
              error
            );
            this.isLoading = false;
          },
        });
    } else {
      console.log('User ID ou option de livraison non définis.');
    }
  }
}
