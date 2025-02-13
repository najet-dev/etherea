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

import { DeliveryAddress } from '../models/DeliveryAddress.model';
import { DeliveryMethod } from '../models/DeliveryMethod.model';
import { PickupPoint } from '../models/pickupPoint.model';
import { Cart } from '../models/cart.model';

import { AppFacade } from 'src/app/services/appFacade.service';
import { AuthService } from 'src/app/services/auth.service';
import { DeliveryMethodService } from 'src/app/services/delivery-method.service';
import { ProductTypeService } from 'src/app/services/product-type.service';
import { CartItemService } from 'src/app/services/cart-item.service';
import { AddDeliveryMethodRequest } from '../models/AddDeliveryMethodRequest .model';

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
  pickupPoints: PickupPoint[] = [];
  selectedPickupPoint: PickupPoint | null = null;
  confirmedPickupPoint: PickupPoint | null = null;
  selectedDeliveryOption: string | null = null;

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

  private loadDeliveryMethods(): void {
    if (!this.userId) return;

    this.isLoading = true;
    this.deliveryMethodService
      .getDeliveryMethods(this.userId)
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

  private loadCartWithDelivery(selectedOption: string): void {
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
  selectPickupPoint(point: PickupPoint): void {
    this.selectedPickupPoint = point;
  }

  confirmPickupPoint(): void {
    const request: AddDeliveryMethodRequest = {
      userId: this.userId,
      deliveryOption: this.selectedDeliveryOption ?? '',
      addressId: this.addressId ?? undefined,
      pickupPointName:
        this.selectedDeliveryOption === 'PICKUP_POINT' &&
        this.selectedPickupPoint
          ? this.selectedPickupPoint.pickupPointName
          : '',
      pickupPointAddress:
        this.selectedDeliveryOption === 'PICKUP_POINT' &&
        this.selectedPickupPoint
          ? this.selectedPickupPoint.pickupPointAddress
          : '',
      pickupPointLatitude:
        this.selectedDeliveryOption === 'PICKUP_POINT' &&
        this.selectedPickupPoint
          ? this.selectedPickupPoint.pickupPointLatitude
          : 0,
      pickupPointLongitude:
        this.selectedDeliveryOption === 'PICKUP_POINT' &&
        this.selectedPickupPoint
          ? this.selectedPickupPoint.pickupPointLongitude
          : 0,
      orderAmount: this.total ?? 0,
    };

    this.deliveryMethodService.addDeliveryMethod(request).subscribe({
      next: (response) => {
        console.log('Méthode de livraison ajoutée avec succès :', response);
      },
      error: (error) => {
        console.error(
          "Erreur lors de l'ajout de la méthode de livraison :",
          error
        );
      },
    });
    this.isModalOpen = false;

    this.cdr.detectChanges();
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.selectedPickupPoint = null;
    this.cdr.detectChanges();
  }

  onDeliveryOptionChange(): void {
    console.log('Option sélectionnée :', this.selectedDeliveryOption);

    if (!this.selectedDeliveryOption) return;

    // Réinitialiser le point relais si le mode change
    if (this.selectedDeliveryOption !== 'PICKUP_POINT') {
      this.selectedPickupPoint = null;
    }

    // Charger les coûts associés au mode sélectionné
    this.loadCartWithDelivery(this.selectedDeliveryOption);

    this.cdr.detectChanges();
  }

  confirmDeliveryOption() {
    this.showPaymentOptions = true;
    this.cdr.detectChanges();
  }

  //payment
  onPaymentMethodSelected(method: string) {
    this.selectedPaymentMethod = method;
  }
}
