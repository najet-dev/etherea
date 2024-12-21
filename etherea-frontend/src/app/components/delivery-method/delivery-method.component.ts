import { Component, inject, OnInit, DestroyRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { switchMap, filter, tap, catchError } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { forkJoin, of } from 'rxjs';
import { Modal } from 'bootstrap';

// Modèles
import { DeliveryAddress } from '../models/DeliveryAddress.model';
import { DeliveryMethod } from '../models/DeliveryMethod.model';
import { PickupPoint } from '../models/pickupPoint.model';
import { Cart } from '../models/cart.model';

// Services
import { AppFacade } from 'src/app/services/appFacade.service';
import { AuthService } from 'src/app/services/auth.service';
import { OrderService } from 'src/app/services/order.service';
import { DeliveryMethodService } from 'src/app/services/delivery-method.service';
import { ProductTypeService } from 'src/app/services/product-type.service';
import { CartCalculationService } from 'src/app/services/cart-calculation.service';
import { CartItemService } from 'src/app/services/cart-item.service';
import { DeliveryOption } from '../models/DeliveryOption.enum';
import { AddDeliveryMethodRequestDTO } from '../models/AddDeliveryMethodRequestDTO .model';

@Component({
  selector: 'app-delivery-method',
  templateUrl: './delivery-method.component.html',
  styleUrls: ['./delivery-method.component.css'],
})
export class DeliveryMethodComponent implements OnInit {
  // Variables de classe
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

  constructor(
    private appFacade: AppFacade,
    private authService: AuthService,
    private route: ActivatedRoute,
    private cartItemService: CartItemService,
    private router: Router,
    private deliveryMethodService: DeliveryMethodService,
    public productTypeService: ProductTypeService
  ) {}

  // Initialisation
  ngOnInit(): void {
    this.loadUserAndAddress();
    this.loadCartTotal();
    this.loadCartItems();
    this.showPickupPoints();
  }

  // Méthodes privées
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

  // Méthodes publiques
  onEditAddress(): void {
    if (this.addressId) {
      this.router.navigate(['/order', this.addressId]);
    } else {
      console.error("L'ID de l'adresse n'est pas défini.");
    }
  }

  showPickupPoints(): void {
    if (!this.userId) return;

    this.deliveryMethodService
      .getPickupMethods(this.userId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (points) => (this.pickupPoints = points),
        error: (error) =>
          this.handleError('récupération des points relais', error),
      });
  }

  selectPickupPoint(point: PickupPoint): void {
    this.selectedPickupPoint = point;
  }

  onDeliveryOptionChange(): void {
    if (!this.selectedDeliveryOption) return;

    this.loadCartWithDelivery(this.selectedDeliveryOption);
  }

  confirmPickupPoint(): void {
    if (this.selectedPickupPoint) {
      this.confirmedPickupPoint = this.selectedPickupPoint;

      const modalElement = document.getElementById('pickupPointModal');
      if (modalElement) {
        const modalInstance =
          Modal.getInstance(modalElement) || new Modal(modalElement);
        modalInstance.hide();
      }

      const body = document.body;
      if (body.classList.contains('modal-open')) {
        body.classList.remove('modal-open');
      }

      const backdrop = document.querySelector('.modal-backdrop');
      if (backdrop) {
        backdrop.remove();
      }
    }
  }

  confirmDeliveryOption() {
    const request: AddDeliveryMethodRequestDTO = {
      userId: this.userId,
      deliveryOption: this.selectedDeliveryOption ?? '',
      addressId: this.addressId ?? undefined,
      pickupPointName: '',
      pickupPointAddress: '',
      pickupPointLatitude: 0,
      pickupPointLongitude: 0,
      orderAmount: 0,
    };

    this.deliveryMethodService.addDeliveryMethod(request).subscribe(
      (response) => {
        console.log('Méthode de livraison ajoutée avec succès :', response);
      },
      (error) => {
        console.error(
          "Erreur lors de l'ajout de la méthode de livraison :",
          error
        );
      }
    );
  }
}
