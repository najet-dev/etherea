import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DeliveryAddressService } from 'src/app/services/delivery-address.service';
import { AuthService } from 'src/app/services/auth.service';
import { filter, map } from 'rxjs';
import { DeliveryAddress } from '../models/deliveryAddress.model';

@Component({
  selector: 'app-address-edit-dialog',
  templateUrl: './address-edit-dialog.component.html',
  styleUrls: ['./address-edit-dialog.component.css'],
})
export class AddressEditDialogComponent {
  addressForm: FormGroup;
  deliveryAddress: DeliveryAddress | null = null;

  constructor(
    private fb: FormBuilder,
    private deliveryAddressService: DeliveryAddressService,
    private authService: AuthService,
    public dialogRef: MatDialogRef<AddressEditDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { isEdit: boolean }
  ) {
    this.addressForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      address: ['', Validators.required],
      zipCode: ['', Validators.required],
      city: ['', Validators.required],
      country: ['', Validators.required],
      phoneNumber: ['', Validators.required],
      isPrimary: [false], // Option pour mettre l'adresse en principale
    });
  }

  saveAddress(): void {
    if (this.addressForm.valid) {
      this.authService
        .getCurrentUser()
        .pipe(
          filter((user) => user !== null),
          map((user) => user?.id)
        )
        .subscribe({
          next: (userId) => {
            if (userId) {
              this.deliveryAddressService
                .addDeliveryAddress(userId, this.addressForm.value)
                .subscribe({
                  next: (newAddress) => {
                    // Mise à jour de l'adresse de livraison pour l'afficher sous la précédente
                    this.deliveryAddress = newAddress;
                    this.dialogRef.close(true);
                  },
                  error: (error) =>
                    console.error('Erreur lors de l’ajout de l’adresse', error),
                });
            }
          },
          error: (error) =>
            console.error(
              "Erreur lors de la récupération de l'utilisateur",
              error
            ),
        });
    }
  }

  close(): void {
    this.dialogRef.close(false);
  }
}
