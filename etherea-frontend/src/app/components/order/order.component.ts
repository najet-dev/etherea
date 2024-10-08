import { Component, OnInit, DestroyRef, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { catchError, tap } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DeliveryAddress } from '../models/DeliveryAddress.model';
import { AuthService } from 'src/app/services/auth.service';
import { AppFacade } from 'src/app/services/appFacade.service';
import { Cart } from '../models/cart.model';
import { forkJoin, of } from 'rxjs';
import { ProductTypeService } from 'src/app/services/product-type.service';

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.css'],
})
export class OrderComponent implements OnInit {
  errorMessage: string = ''; // Pour les erreurs
  deliveryAddressForm!: FormGroup;
  submitted = false;
  private destroyRef = inject(DestroyRef);
  userId: number | null = null;
  cartItems: Cart[] = [];
  cartTotal: number = 0;
  isCartEmpty: boolean = true;

  public errorMessages = {
    firstName: [{ type: 'required', message: 'Prénom requis' }],
    lastName: [{ type: 'required', message: 'Nom requis' }],
    address: [{ type: 'required', message: 'Adresse requise' }],
    zipCode: [
      { type: 'required', message: 'Code postal requis' },
      { type: 'min', message: 'Le code postal doit être valide' },
    ],
    city: [{ type: 'required', message: 'Ville requise' }],
    country: [{ type: 'required', message: 'Pays requis' }],
    phoneNumber: [{ type: 'required', message: 'Numéro de téléphone requis' }],
  };

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private appFacade: AppFacade,
    private router: Router,
    public productTypeService: ProductTypeService
  ) {}

  ngOnInit() {
    this.authService.AuthenticatedUser$.pipe(
      tap((user) => {
        if (user) {
          this.userId = user.id;
          this.loadCartItems();
        } else {
          this.userId = null;
        }
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe();

    this.deliveryAddressForm = this.formBuilder.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      address: ['', [Validators.required]],
      zipCode: ['', [Validators.required, Validators.min(1000)]],
      city: ['', [Validators.required]],
      country: ['', [Validators.required]],
      phoneNumber: ['', [Validators.required]],
    });
  }

  onSubmit() {
    this.submitted = true;

    if (this.deliveryAddressForm.invalid) {
      return;
    }

    if (this.userId) {
      const deliveryAddress: DeliveryAddress = this.deliveryAddressForm.value;
      this.appFacade
        .addDeliveryAddress(this.userId, deliveryAddress)
        .pipe(
          tap({
            next: () => {
              this.router.navigate(['/deliveryMethod']);
              this.deliveryAddressForm.reset();
            },
            error: () => {
              this.errorMessage =
                'Une erreur est survenue. Veuillez réessayer plus tard.';
            },
          }),
          takeUntilDestroyed(this.destroyRef)
        )
        .subscribe();
    } else {
      this.errorMessage = 'Utilisateur non trouvé. Veuillez vous reconnecter.';
    }
  }

  loadCartItems() {
    if (!this.userId) return;

    this.appFacade.getCartItems(this.userId).subscribe((cartItems) => {
      this.cartItems = cartItems;
      this.isCartEmpty = this.cartItems.length === 0;

      const productObservables = this.cartItems.map((item) =>
        this.appFacade.getProductById(item.productId).pipe(
          tap((product) => {
            if (product) {
              item.product = product;
              this.initializeSelectedVolume(item);
            }
          }),
          catchError((error) => {
            console.error('Erreur lors de la récupération du produit :', error);
            return of(null);
          })
        )
      );

      forkJoin(productObservables).subscribe(() => {
        this.calculateCartTotal();
      });
    });
  }

  initializeSelectedVolume(item: Cart): void {
    if (!item.selectedVolume && item.volume) {
      item.selectedVolume = { ...item.volume };
    }

    if (
      this.productTypeService.isHairProduct(item.product) &&
      item.selectedVolume
    ) {
      const selectedVol = item.product.volumes?.find(
        (vol) => vol.id === item.selectedVolume?.id
      );
      item.selectedVolume = selectedVol || item.selectedVolume;
    }
  }

  calculateCartTotal(): void {
    this.cartTotal = this.cartItems.reduce((total, item) => {
      if (item.product) {
        if (
          this.productTypeService.isHairProduct(item.product) &&
          item.selectedVolume
        ) {
          item.subTotal = item.selectedVolume.price * item.quantity;
        } else if (this.productTypeService.isFaceProduct(item.product)) {
          item.subTotal = item.product.basePrice * item.quantity;
        }
        return total + (item.subTotal || 0);
      }
      return total;
    }, 0);
  }
}
