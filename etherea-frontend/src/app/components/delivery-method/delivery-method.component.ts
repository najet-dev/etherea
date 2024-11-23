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
  deliveryMethod: DeliveryMethod[] = []; // Stocker les méthodes de livraison
  pickupPoints: PickupPoint[] = []; // Liste des points relais
  selectedPickupPoint: PickupPoint | null = null; // Point relais sélectionné
  selectedDeliveryOption: string | null = null; // Option de livraison sélectionnée

  private destroyRef = inject(DestroyRef);

  constructor(
    private appFacade: AppFacade,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private deliveryMethodService: DeliveryMethodService // Injection du service de méthodes de livraison
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
  }

  // Méthode pour charger les modes de livraison
  loadDeliveryMethods(): void {
    if (this.userId) {
      this.isLoading = true; // Mettre en chargement les modes de livraison
      this.deliveryMethodService
        .getDeliveryMethods(this.userId) // Appel au service pour récupérer les méthodes
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (methods) => {
            console.log('Méthodes de livraison récupérées:', methods); // Débogage pour vérifier la réponse
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

  confirmPickupPoint(): void {
    if (this.selectedPickupPoint) {
      console.log('Point relais confirmé:', this.selectedPickupPoint);
      // Ajouter ici la logique pour associer le point relais sélectionné à l'utilisateur
    }
  }
}
