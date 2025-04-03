import {
  Component,
  inject,
  OnInit,
  DestroyRef,
  ChangeDetectorRef,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { switchMap, filter } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { of } from 'rxjs';

import { DeliveryAddress } from '../models/deliveryAddress.model';
import { DeliveryMethod } from '../models/deliveryMethod.model';
import { PickupPointDetails } from '../models/pickupPointDetails.model';
import { Cart } from '../models/cart.model';

import { AppFacade } from 'src/app/services/appFacade.service';
import { AuthService } from 'src/app/services/auth.service';
import { DeliveryMethodService } from 'src/app/services/delivery-method.service';
import { ProductTypeService } from 'src/app/services/product-type.service';
import { CartItemService } from 'src/app/services/cart-item.service';
import { AddDeliveryMethodRequest } from '../models/addDeliveryMethodRequest.model';
import { UpdateDeliveryMethodRequest } from '../models/updateDeliveryMethodRequest.model';
import { DeliveryType } from '../models/deliveryType.model';
import {
  DeliveryName,
  DeliveryNameTranslations,
} from '../models/deliveryName.enum';

@Component({
  selector: 'app-delivery-method',
  templateUrl: './delivery-method.component.html',
  styleUrls: ['./delivery-method.component.css'],
})
export class DeliveryMethodComponent implements OnInit {
  deliveryAddress: DeliveryAddress | null = null;
  userId: number = 0;
  addressId: number | null = null;
  firstName: string | null = null;
  lastName: string | null = null;

  isLoading: boolean = true;
  deliveryMethod: DeliveryMethod[] = [];
  deliveryType: DeliveryType[] = [];
  pickupPoints: PickupPointDetails[] = [];
  selectedPickupPoint: PickupPointDetails | null = null;
  confirmedPickupPoint: PickupPointDetails | null = null;
  selectedDeliveryOption: DeliveryName | null = null;

  cartTotal: number | null = null;
  deliveryCost: number | null = null;
  total: number | null = null;
  cartItems: Cart[] = [];
  isCartEmpty: boolean = true;
  errorMessage: string = '';

  private destroyRef = inject(DestroyRef);
  isProcessing: boolean = false;
  paymentConfirmed: boolean = false;
  showPaymentOptions = false;
  selectedPaymentMethod: string | null = null;
  isLoadingPickupPoints = false;
  isModalOpen = false;
  isEditingDelivery: boolean = false;

  deliveryMethodId!: number;
  deliveryTypeId!: number;
  selectedDeliveryType!: DeliveryType;
  selectedDeliveryMethod!: DeliveryMethod;
  DeliveryNameTranslations = DeliveryNameTranslations;

  constructor(
    private appFacade: AppFacade,
    private authService: AuthService,
    private route: ActivatedRoute,
    private cartItemService: CartItemService,
    private router: Router,
    private deliveryMethodService: DeliveryMethodService,
    public productTypeService: ProductTypeService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadUserAndAddress();
    this.loadDeliveryMethods();
    this.loadDeliveryTypes();
    this.loadCartTotal();
    this.loadCartItems();
    this.loadPickupPoints();
  }

  private loadUserAndAddress(): void {
    this.route.paramMap
      .pipe(
        filter((params) => !!params.get('addressId')),
        switchMap((params) => {
          const addressId = +params.get('addressId')!;
          this.addressId = addressId;
          return this.authService.getCurrentUser().pipe(
            filter((user) => !!user?.id),
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
          this.firstName = address.user?.firstName || null;
          this.lastName = address.user?.lastName || null;
          this.isLoading = false;
          this.loadDeliveryMethods();
        },
        error: (error) => this.handleError('récupération de l’adresse', error),
      });
  }

  private loadCartItems(): void {
    this.cartItemService.loadCartItems(this.userId).subscribe({
      next: (cartItems) => {
        this.cartItems = cartItems;
        this.isCartEmpty = !cartItems.length;
      },
      error: (err) => {
        this.errorMessage = 'Impossible de charger les articles du panier.';
        console.error(err);
      },
    });
  }

  private loadCartTotal(): void {
    if (!this.userId) return;

    this.deliveryMethodService
      .getCartTotal(this.userId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (total) => {
          this.cartTotal = total || 0;
          this.deliveryCost = 0;
          this.total = this.cartTotal;
        },
        error: (error) =>
          this.handleError('chargement du total du panier', error),
      });
  }
  private loadDeliveryTypes(): void {
    if (!this.userId) return;

    this.isLoading = true;
    this.appFacade
      .getDeliveryTypes(this.userId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (types) => {
          this.deliveryType = types;
          this.isLoading = false;
        },
        error: (error) =>
          this.handleError('chargement des modes de livraison', error),
      });
  }

  private loadDeliveryMethods(): void {
    if (!this.userId) return;

    this.isLoading = true;
    this.appFacade
      .getUserDeliveryMethods(this.userId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (methods) => {
          this.deliveryMethod = methods;
          this.isLoading = false;
        },
        error: (error) =>
          this.handleError('chargement des modes de livraison', error),
      });
  }

  private loadCartWithDelivery(selectedOption: DeliveryName): void {
    if (!this.userId) return;

    this.isLoading = true;
    this.deliveryMethodService
      .getCartWithDelivery(this.userId, selectedOption)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => {
          this.cartTotal = data.cartTotal || 0;
          this.deliveryCost = data.deliveryCost || 0;
          this.total = this.cartTotal + this.deliveryCost;
          this.isLoading = false;
        },
        error: (error) =>
          this.handleError('calcul des coûts de livraison', error),
      });
  }

  private loadPickupPoints(): void {
    if (!this.userId) return;

    this.deliveryMethodService.getPickupMethods(this.userId).subscribe({
      next: (points) => {
        this.pickupPoints = points;
      },
      error: (error) =>
        this.handleError('récupération des points relais', error),
    });
  }

  private handleError(context: string, error?: unknown) {
    if (error instanceof Error) {
      console.error(`Erreur lors de ${context}:`, error.message);
    } else {
      console.error(`Erreur lors de ${context}:`, error);
    }

    this.errorMessage =
      'Une erreur est survenue. Veuillez réessayer plus tard.';
    return of(null);
  }

  onEditAddress(): void {
    if (this.addressId) {
      this.router.navigate(['/order', this.addressId]);
    } else {
      console.error("L'ID de l'adresse n'est pas défini.");
    }
  }

  openModal(): void {
    this.isModalOpen = true;
    this.isLoadingPickupPoints = true;

    this.deliveryMethodService.getPickupMethods(this.userId).subscribe({
      next: (points) => {
        this.pickupPoints = points;
        this.isLoadingPickupPoints = false;
      },
      error: (error) => {
        console.error('Erreur chargement points relais:', error);
        this.isLoadingPickupPoints = false;
      },
    });
  }
  selectPickupPoint(point: PickupPointDetails): void {
    this.selectedPickupPoint = point;
    console.log('Point relais sélectionné:', this.selectedPickupPoint);
  }

  confirmPickupPoint(): void {
    if (this.selectedPickupPoint) {
      this.confirmedPickupPoint = this.selectedPickupPoint;

      console.log('Point relais confirmé:', this.confirmedPickupPoint);

      // Save delivery method only after confirmation
      this.confirmDeliveryOption();
    }
    this.isModalOpen = false;
    this.cdr.detectChanges();
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.selectedPickupPoint = null;
    this.cdr.detectChanges();
  }

  onDeliveryOptionChange(selectedType: DeliveryName) {
    this.selectedDeliveryOption = selectedType;

    this.selectedDeliveryType =
      this.deliveryType.find(
        (delivery) => delivery.deliveryName === selectedType
      ) || ({} as DeliveryType);

    console.log('Delivery Type sélectionné :', this.selectedDeliveryType);

    if (this.selectedDeliveryType) {
      this.deliveryCost = this.selectedDeliveryType.cost;
    } else {
      this.deliveryCost = 0;
    }

    if (this.selectedDeliveryOption === DeliveryName.PICKUP_POINT) {
      this.refreshPickupPoints();
    }

    if (this.selectedDeliveryOption !== DeliveryName.PICKUP_POINT) {
      this.isEditingDelivery
        ? this.updateDeliveryOption()
        : this.confirmDeliveryOption();
    }
  }

  private refreshPickupPoints(): void {
    this.selectedPickupPoint = null; // Reset the selected pickup point
    this.confirmedPickupPoint = null;
    this.isLoadingPickupPoints = true;

    this.deliveryMethodService.getPickupMethods(this.userId).subscribe({
      next: (points) => {
        this.pickupPoints = points;
        this.isLoadingPickupPoints = false;
        this.cdr.detectChanges(); // Force display update
      },
      error: (error) => {
        console.error(
          'Erreur lors du rafraîchissement des points relais:',
          error
        );
        this.isLoadingPickupPoints = false;
      },
    });
  }

  confirmDeliveryOption(): void {
    if (!this.selectedDeliveryOption) {
      this.errorMessage = 'Veuillez sélectionner un mode de livraison.';
      return;
    }

    if (!this.selectedDeliveryType || !this.selectedDeliveryType.id) {
      console.error('Erreur : deliveryTypeId est null');
      this.errorMessage = 'Veuillez sélectionner un mode de livraison valide.';
      return;
    }

    const request: AddDeliveryMethodRequest = {
      userId: this.userId,
      deliveryTypeId: this.selectedDeliveryType.id,
      addressId: this.addressId ?? undefined,
      pickupPointName:
        this.selectedDeliveryOption === DeliveryName.PICKUP_POINT &&
        this.selectedPickupPoint
          ? this.selectedPickupPoint.pickupPointName
          : '',
      pickupPointAddress:
        this.selectedDeliveryOption === DeliveryName.PICKUP_POINT &&
        this.selectedPickupPoint
          ? this.selectedPickupPoint.pickupPointAddress
          : '',
      pickupPointLatitude:
        this.selectedDeliveryOption === DeliveryName.PICKUP_POINT &&
        this.selectedPickupPoint
          ? this.selectedPickupPoint.pickupPointLatitude
          : 0,
      pickupPointLongitude:
        this.selectedDeliveryOption === DeliveryName.PICKUP_POINT &&
        this.selectedPickupPoint
          ? this.selectedPickupPoint.pickupPointLongitude
          : 0,
      orderAmount: this.total ?? 0,
    };

    console.log('Request sent to API:', request);

    // Check if a delivery method already exists for the user
    if (this.deliveryMethodId) {
      console.log(
        'Méthode de livraison existante détectée, mise à jour en cours...'
      );

      const updateRequest: UpdateDeliveryMethodRequest = {
        ...request,
        deliveryMethodId: this.deliveryMethodId, // Add existing ID
      };

      this.deliveryMethodService
        .updateDeliveryMethod(this.deliveryMethodId, updateRequest)
        .subscribe({
          next: () => {
            console.log('Méthode de livraison mise à jour avec succès');
            this.isEditingDelivery = false;
            this.loadDeliveryMethods(); // Refresh methods after update
          },
          error: (error) => {
            console.error(
              'Erreur lors de la mise à jour de la méthode de livraison :',
              error
            );
          },
        });
    } else {
      console.log('Aucune méthode existante, ajout d’une nouvelle méthode...');

      this.deliveryMethodService.addDeliveryMethod(request).subscribe({
        next: (response) => {
          console.log('Méthode de livraison ajoutée avec succès :', response);

          if (response?.id) {
            this.deliveryMethodId = response.id;
            console.log(
              'ID de la méthode de livraison sélectionnée:',
              this.deliveryMethodId
            );
          } else {
            console.error("Erreur : l'ID de la méthode ajoutée est manquant !");
          }
        },
        error: (error) => {
          console.error(
            "Erreur lors de l'ajout de la méthode de livraison :",
            error
          );
        },
      });
    }
  }

  onEditDeliveryMethod(deliveryMethodId: number): void {
    this.isEditingDelivery = true;
    this.deliveryMethodId = deliveryMethodId;
    this.selectedDeliveryOption = null;

    console.log(
      'ID de la méthode de livraison sélectionnée:',
      this.deliveryMethodId
    ); // Debug
  }

  updateDeliveryOption(): void {
    if (!this.deliveryMethodId) {
      console.error('Erreur : deliveryMethodId est indéfini !');
      this.errorMessage = 'Une erreur est survenue. Veuillez réessayer.';
      return;
    }

    if (!this.selectedDeliveryOption) {
      this.errorMessage = 'Veuillez sélectionner un mode de livraison.';
      return;
    }

    if (!this.selectedDeliveryType) {
      console.error('Erreur : Aucun type de livraison sélectionné !');
      this.errorMessage = 'Veuillez sélectionner un mode de livraison valide.';
      return;
    }

    const request: UpdateDeliveryMethodRequest = {
      deliveryMethodId: this.deliveryMethodId,
      userId: this.userId,
      deliveryTypeId: this.selectedDeliveryType?.id ?? null,
      addressId: this.addressId ?? undefined,
      pickupPointName:
        this.selectedDeliveryOption === DeliveryName.PICKUP_POINT &&
        this.selectedPickupPoint
          ? this.selectedPickupPoint.pickupPointName
          : '',
      pickupPointAddress:
        this.selectedDeliveryOption === DeliveryName.PICKUP_POINT &&
        this.selectedPickupPoint
          ? this.selectedPickupPoint.pickupPointAddress
          : '',
      pickupPointLatitude:
        this.selectedDeliveryOption === DeliveryName.PICKUP_POINT &&
        this.selectedPickupPoint
          ? this.selectedPickupPoint.pickupPointLatitude
          : 0,
      pickupPointLongitude:
        this.selectedDeliveryOption === DeliveryName.PICKUP_POINT &&
        this.selectedPickupPoint
          ? this.selectedPickupPoint.pickupPointLongitude
          : 0,
    };

    this.appFacade
      .updateDeliveryMethod(this.deliveryMethodId, request)
      .subscribe({
        next: () => {
          console.log('Méthode de livraison mise à jour avec succès');
          this.isEditingDelivery = false;
          this.loadDeliveryMethods();
        },
        error: (error) =>
          this.handleError('mise à jour du mode de livraison', error),
      });
  }

  //payment
  onPaymentMethodSelected(method: string) {
    this.selectedPaymentMethod = method;
  }
}
