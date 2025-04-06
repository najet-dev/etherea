import { Component, DestroyRef, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { catchError, map, of, tap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DeliveryAddress } from '../models/deliveryAddress.model';
import { AppFacade } from 'src/app/services/appFacade.service';
import { Cart } from '../models/cart.model';
import { CartItemService } from 'src/app/services/cart-item.service';

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
    public cartItemService: CartItemService
  ) {}

  ngOnInit() {
    this.initializeForm();

    this.appFacade
      .getCurrentUserDetails() // Utilisation de la nouvelle méthode
      .pipe(
        tap((user) => {
          console.log('Utilisateur récupéré:', user);
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
    console.log("Formulaire d'adresse initialisé:", this.deliveryAddressForm);
  }

  loadUserDetails(userId: number) {
    console.log("Chargement des détails de l'utilisateur:", userId);
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
          if (addresses.length > 0) {
            this.populateAddressForm(addresses[0]);
          }
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
    console.log("Chargement des données du panier pour l'utilisateur:", userId);
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
    console.log('Formulaire soumis');

    // Vérification de l'état du formulaire
    if (this.deliveryAddressForm.invalid) {
      this.errorMessage =
        'Veuillez remplir correctement tous les champs requis.';
      console.log('Formulaire invalide:', this.deliveryAddressForm);
      return;
    }

    // Vérification de l'ID utilisateur avant la soumission
    if (!this.userId) {
      console.log(
        "Utilisateur non authentifié, redirection vers la page d'accueil"
      );
      this.router.navigate(['/']); // Redirection vers la page d'accueil si pas d'utilisateur
      return;
    }

    const deliveryAddress: DeliveryAddress = {
      ...this.deliveryAddressForm.value,
      userId: this.userId,
    };

    console.log('Adresse de livraison à vérifier:', deliveryAddress);

    this.checkIfAddressExists(deliveryAddress).subscribe(
      (existingAddressId) => {
        console.log("ID de l'adresse existante:", existingAddressId);
        if (existingAddressId) {
          console.log(
            "Redirection vers la méthode de livraison pour l'adresse existante"
          );
          this.router.navigate(['/deliveryMethod', existingAddressId]);
        } else if (this.addressId) {
          console.log("Mise à jour de l'adresse existante");
          this.updateAddress(deliveryAddress);
        } else {
          console.log('Ajout de la nouvelle adresse');
          this.addAddress(deliveryAddress);
        }
      }
    );
  }

  addAddress(deliveryAddress: DeliveryAddress) {
    console.log("Ajout d'une nouvelle adresse:", deliveryAddress);
    this.appFacade
      .addDeliveryAddress(this.userId as number, deliveryAddress)
      .pipe(
        tap((response) => {
          console.log("Réponse après ajout d'adresse:", response);
          if (response?.id) {
            this.router.navigate(['/deliveryMethod', response.id]);
          } else {
            this.errorMessage = "Erreur: L'ID de l'adresse est indéfini.";
          }
        }),
        catchError(() => this.handleError('Ajout adresse'))
      )
      .subscribe();
  }

  updateAddress(deliveryAddress: DeliveryAddress) {
    console.log("Mise à jour de l'adresse:", deliveryAddress);
    this.appFacade
      .updateDeliveryAddress(this.userId as number, {
        ...deliveryAddress,
        id: this.addressId,
      })
      .pipe(
        tap((response) => {
          console.log("Réponse après mise à jour d'adresse:", response);
          if (response?.id) {
            this.router.navigate(['/deliveryMethod', response.id]);
          } else {
            this.errorMessage = "Erreur: L'ID de l'adresse est indéfini.";
          }
        }),
        catchError(() => this.handleError('Mise à jour adresse'))
      )
      .subscribe();
  }

  checkIfAddressExists(address: DeliveryAddress) {
    console.log("Vérification si l'adresse existe déjà:", address);
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
    console.log('Popup récapitulatif affichée:', this.showSummaryPopup);
  }
}
