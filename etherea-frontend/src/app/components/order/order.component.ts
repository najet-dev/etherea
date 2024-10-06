import { Component, OnInit, DestroyRef, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DeliveryAddress } from '../models/DeliveryAddress.model';
import { AuthService } from 'src/app/services/auth.service';
import { AppFacade } from 'src/app/services/appFacade.service';

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
    private router: Router
  ) {}

  ngOnInit() {
    this.authService.AuthenticatedUser$.pipe(
      tap((user) => {
        if (user) {
          this.userId = user.id;
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
}
