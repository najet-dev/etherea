import { Component, inject, OnInit, DestroyRef } from '@angular/core';
import { DeliveryAddress } from '../models/deliveryAddress.model';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';
import { catchError, of, switchMap, filter } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DeliveryAddressService } from 'src/app/services/delivery-address.service';
import { MatDialog } from '@angular/material/dialog';
import { AddressEditDialogComponent } from '../address-edit-dialog/address-edit-dialog.component';

@Component({
  selector: 'app-addresses',
  templateUrl: './addresses.component.html',
  styleUrls: ['./addresses.component.css'],
})
export class AddressesComponent {
  deliveryAddresses: DeliveryAddress[] = [];
  userId: number = 0;
  isLoading: boolean = true;
  errorMessage: string = '';
  isModalOpen: boolean = false;

  private destroyRef = inject(DestroyRef);

  constructor(
    private authService: AuthService,
    private route: ActivatedRoute,
    private deliveryAddressService: DeliveryAddressService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadUserAndAddress();
  }

  private loadUserAndAddress(): void {
    this.authService
      .getCurrentUser()
      .pipe(
        filter((user) => !!user?.id),
        switchMap((user) => {
          this.userId = user!.id;
          return this.deliveryAddressService
            .getUserDeliveryAddresses(this.userId)
            .pipe(
              catchError(() => {
                this.errorMessage =
                  'Erreur lors de la récupération des adresses.';
                return of([]);
              })
            );
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (addresses) => {
          const mainAddress = addresses.find((a) => a.default);
          const others = addresses.filter((a) => !a.default);

          this.deliveryAddresses = mainAddress
            ? [mainAddress, ...others]
            : others;

          if (mainAddress) {
            this.deliveryAddressService.setDefaultAddressState(mainAddress);
          }

          this.isLoading = false;
        },
        error: (error) => this.handleError('récupération des adresses', error),
      });
  }

  private handleError(context: string, error?: unknown): void {
    console.error(`Erreur lors de ${context}:`, error);
    this.errorMessage =
      'Une erreur est survenue. Veuillez réessayer plus tard.';
  }

  openAddAddressDialog(): void {
    const dialogRef = this.dialog.open(AddressEditDialogComponent, {
      width: '500px',
      data: { isEdit: false },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loadUserAndAddress(); // Rafraîchit la liste des adresses après ajout
      }
    });
  }

  setAsDefault(addressId: number): void {
    if (!this.userId) return;

    this.deliveryAddressService
      .setDefaultAddress(this.userId, addressId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          // Mise à jour immédiate locale sans attendre la réponse API
          this.deliveryAddresses = this.deliveryAddresses.map((address) => ({
            ...address,
            default: address.id === addressId,
          }));

          //principale en haut
          const mainAddress = this.deliveryAddresses.find(
            (addr) => addr.default
          );
          const others = this.deliveryAddresses.filter((addr) => !addr.default);
          this.deliveryAddresses = mainAddress
            ? [mainAddress, ...others]
            : others;

          // Mise à jour du state global
          if (mainAddress) {
            this.deliveryAddressService.setDefaultAddressState(mainAddress);
          }
        },
        error: (error) =>
          this.handleError("Définition de l'adresse principale", error),
      });
  }
  editAddress(address: DeliveryAddress): void {
    console.log('Adresse à modifier :', address);

    const dialogRef = this.dialog.open(AddressEditDialogComponent, {
      width: '500px',
      data: {
        isEdit: true,
        address: { ...address }, // On clone pour ne pas modifier directement
      },
    });
    console.log('Popup ouverte avec les données :', dialogRef);

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loadUserAndAddress(); // On recharge les adresses après modification
      }
    });
  }
  deleteAddress(addressId: number): void {}
}
