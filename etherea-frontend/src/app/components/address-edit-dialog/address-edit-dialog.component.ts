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
  userId!: number;
  formSubmitted = false;

  constructor(
    private fb: FormBuilder,
    private deliveryAddressService: DeliveryAddressService,
    private authService: AuthService,
    public dialogRef: MatDialogRef<AddressEditDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: { isEdit: boolean; address?: DeliveryAddress }
  ) {
    this.addressForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      address: ['', Validators.required],
      zipCode: ['', [Validators.required, Validators.pattern(/^\d{5}$/)]],
      city: ['', Validators.required],
      country: ['', Validators.required],
      phoneNumber: [
        '',
        [Validators.required, Validators.pattern(/^(\+33|0)[1-9][0-9]{8}$/)],
      ],
    });

    if (this.data.isEdit && this.data.address) {
      this.addressForm.patchValue(this.data.address);
      this.deliveryAddress = this.data.address;
    }

    this.authService
      .getCurrentUser()
      .pipe(
        filter((user) => !!user?.id),
        map((user) => user!.id)
      )
      .subscribe((id) => (this.userId = id));
  }

  isFieldInvalid(field: string): boolean {
    const control = this.addressForm.get(field);
    return (
      !!control && control.invalid && (control.touched || this.formSubmitted)
    );
  }

  saveAddress(): void {
    this.formSubmitted = true;

    if (this.addressForm.invalid) return;

    const addressData = {
      ...this.addressForm.value,
      id: this.deliveryAddress?.id,
    };

    if (this.data.isEdit && this.deliveryAddress) {
      this.deliveryAddressService
        .updateDeliveryAddress(this.userId, addressData)
        .subscribe({
          next: () => this.dialogRef.close(true),
          error: (err) => console.error('Erreur lors de la modification', err),
        });
    } else {
      this.deliveryAddressService
        .addDeliveryAddress(this.userId, addressData)
        .subscribe({
          next: () => this.dialogRef.close(true),
          error: (err) => console.error("Erreur lors de l'ajout", err),
        });
    }
  }

  close(): void {
    this.dialogRef.close(false);
  }
}
