import { Component, DestroyRef, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { catchError, map, of, tap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DeliveryAddress } from '../models/deliveryAddress.model';
import { AppFacade } from 'src/app/services/appFacade.service';
import { Cart } from '../models/cart.model';
import { CartItemService } from 'src/app/services/cart-item.service';
import { DeliveryAddressService } from 'src/app/services/delivery-address.service';

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.css'],
})
export class OrderComponent implements OnInit {
  errorMessage: string = '';
  deliveryAddressForm!: FormGroup;
  submitted = false;
  private destroyRef = inject(DestroyRef);
  userId: number | null = null;
  addressId: number = 0;

  cartItems: Cart[] = [];
  cartTotal: number = 0;
  isCartEmpty: boolean = true;
  existingAddresses: DeliveryAddress[] = [];
  showSummaryPopup: boolean = false;

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
    private appFacade: AppFacade,
    private router: Router,
    private route: ActivatedRoute,
    public cartItemService: CartItemService,
    private deliveryAddressService: DeliveryAddressService
  ) {}

  ngOnInit() {
    this.deliveryAddressService.resetAddresses();
    this.initializeForm();

    this.appFacade
      .getCurrentUserDetails()
      .pipe(
        tap((user) => {
          if (user) {
            this.userId = user.id;
            console.log('ID utilisateur défini:', this.userId);
            this.loadUserDetails(user.id);
          }
        }),
        catchError((error) => this.handleError('Détails utilisateur', error))
      )
      .subscribe();
  }

  initializeForm() {
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

  loadUserDetails(userId: number) {
    this.appFacade
      .getUserDetails(userId)
      .pipe(
        tap((user) => {
          if (user) {
            console.log('Détails utilisateur chargés:', user);
            this.deliveryAddressForm.patchValue({
              firstName: user.firstName,
              lastName: user.lastName,
            });
            this.loadExistingAddresses(userId);
            this.loadCartData(userId);
          }
        }),
        takeUntilDestroyed(this.destroyRef),
        catchError((error) => this.handleError('Détails utilisateur', error))
      )
      .subscribe();
  }

  loadExistingAddresses(userId: number) {
    console.log(
      "Chargement des adresses existantes pour l'utilisateur:",
      userId
    );

    this.appFacade
      .getUserDeliveryAddresses(userId)
      .pipe(
        tap((addresses: DeliveryAddress[]) => {
          console.log('Adresses existantes récupérées:', addresses);
          this.existingAddresses = addresses;

          this.deliveryAddressService.defaultAddress$
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe((defaultAddress) => {
              if (defaultAddress) {
                console.log('Adresse par défaut trouvée:', defaultAddress);
                this.populateAddressForm(defaultAddress);
              } else if (addresses.length > 0) {
                this.populateAddressForm(addresses[0]); // fallback
              }
            });
        }),
        catchError((error) => this.handleError('Adresses existantes', error))
      )
      .subscribe();
  }

  populateAddressForm(address: DeliveryAddress) {
    console.log('Adresse à remplir dans le formulaire:', address);
    this.addressId = address.id;
    this.deliveryAddressForm.patchValue({
      firstName: address.user.firstName,
      lastName: address.user.lastName,
      address: address.address,
      zipCode: address.zipCode,
      city: address.city,
      country: address.country,
      phoneNumber: address.phoneNumber,
    });
  }

  loadCartData(userId: number) {
    this.cartItemService.loadCartItems(userId).subscribe({
      next: () => {
        this.cartItemService.cartTotal.subscribe((total) => {
          console.log('Total du panier:', total);
          this.cartTotal = total;
        });
        this.cartItems = this.cartItemService.cartItems;
        this.isCartEmpty = !this.cartItems.length;
        console.log('Éléments du panier:', this.cartItems);
        console.log('Panier vide:', this.isCartEmpty);
      },
      error: () => this.handleError('Chargement du panier'),
    });
  }

  onSubmit() {
    this.submitted = true;

    // Vérifie si le formulaire est valide
    if (this.deliveryAddressForm.invalid) {
      this.errorMessage =
        'Veuillez remplir correctement tous les champs requis.';
      console.warn('Formulaire invalide:', this.deliveryAddressForm);
      return;
    }

    // Vérifie si l'utilisateur est authentifié
    if (!this.userId) {
      this.errorMessage =
        'Utilisateur non authentifié. Veuillez vous reconnecter.';
      console.warn('Utilisateur non authentifié, arrêt de la soumission.');
      return;
    }

    const deliveryAddress: DeliveryAddress = {
      ...this.deliveryAddressForm.value,
      userId: this.userId,
    };

    this.checkIfAddressExists(deliveryAddress)
      .pipe(
        tap((existingAddressId) => {
          console.log(
            'Résultat vérification adresse existante :',
            existingAddressId
          );

          if (existingAddressId) {
            this.router.navigate(['/deliveryMethod', existingAddressId]);
          } else if (this.addressId) {
            console.log('Mise à jour adresse existante');
            this.updateAddress(deliveryAddress);
          } else {
            console.log('Ajout nouvelle adresse');
            this.addAddress(deliveryAddress);
          }
        }),
        catchError((error) => {
          if (error?.status === 401) {
            this.errorMessage =
              'Votre session a expiré. Veuillez vous reconnecter.';
          } else {
            this.errorMessage =
              "Une erreur est survenue lors de l'envoi du formulaire.";
            console.error('Erreur lors de la soumission :', error);
          }
          return of(null);
        })
      )
      .subscribe();
  }

  addAddress(deliveryAddress: DeliveryAddress) {
    this.appFacade
      .addDeliveryAddress(this.userId as number, deliveryAddress)
      .pipe(
        tap((response) => {
          if (response?.id) {
            this.router.navigate(['/deliveryMethod', response.id]);
            console.log("Réponse après ajout d'adresse:", response);
          } else {
            this.errorMessage = "Erreur: L'ID de l'adresse est indéfini.";
          }
        }),
        catchError(() => this.handleError('Ajout adresse'))
      )
      .subscribe();
  }

  updateAddress(deliveryAddress: DeliveryAddress) {
    this.appFacade
      .updateDeliveryAddress(this.userId as number, {
        ...deliveryAddress,
        id: this.addressId,
      })
      .pipe(
        tap((response) => {
          if (response?.id) {
            this.router.navigate(['/deliveryMethod', response.id]);
            console.log("Réponse après ajout d'adresse:", response);
          } else {
            this.errorMessage = "Erreur: L'ID de l'adresse est indéfini.";
          }
        }),
        catchError(() => this.handleError('Mise à jour adresse'))
      )
      .subscribe();
  }

  checkIfAddressExists(address: DeliveryAddress) {
    if (!this.userId) return of(null);
    return this.appFacade.getUserDeliveryAddresses(this.userId).pipe(
      map((addresses) => {
        const existingAddress = addresses.find(
          (existing) =>
            existing.id !== this.addressId &&
            existing.address === address.address &&
            existing.zipCode === address.zipCode &&
            existing.city === address.city &&
            existing.country === address.country &&
            existing.phoneNumber == address.phoneNumber
        );
        return existingAddress ? existingAddress.id : null;
      }),
      catchError(() => of(null))
    );
  }

  handleError(context: string, error?: unknown) {
    console.log(`Erreur survenue dans ${context}:`, error);
    if (error instanceof Error) {
      console.error(`Erreur lors de ${context}:`, error.message);
    } else {
      console.error(`Erreur lors de ${context}:`, error);
    }

    this.errorMessage =
      'Une erreur est survenue. Veuillez réessayer plus tard.';
    return of(null);
  }

  toggleSummaryPopup() {
    this.showSummaryPopup = !this.showSummaryPopup;
  }
}
