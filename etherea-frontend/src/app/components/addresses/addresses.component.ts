import { Component, inject, OnInit, DestroyRef } from '@angular/core';
import { DeliveryAddress } from '../models/deliveryAddress.model';
import { AuthService } from 'src/app/services/auth.service';
import { catchError, of, switchMap, filter } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { AddressEditDialogComponent } from '../address-edit-dialog/address-edit-dialog.component';
import { AppFacade } from 'src/app/services/appFacade.service';

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
  successMessage = ''; // Variable pour le message de succès

  private destroyRef = inject(DestroyRef);

  constructor(
    private authService: AuthService,
    private appfacade: AppFacade,
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
          return this.appfacade.getUserDeliveryAddresses(this.userId).pipe(
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
            this.appfacade.setDefaultAddressState(mainAddress);
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

  addNewAddress(): void {
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

    this.appfacade
      .setDefaultAddress(this.userId, addressId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          // Mise à jour immédiate locale sans attendre la réponse API
          this.deliveryAddresses = this.deliveryAddresses.map((address) => ({
            ...address,
            default: address.id === addressId,
          }));

          //adrresse principale en haut
          const mainAddress = this.deliveryAddresses.find(
            (addr) => addr.default
          );
          const others = this.deliveryAddresses.filter((addr) => !addr.default);
          this.deliveryAddresses = mainAddress
            ? [mainAddress, ...others]
            : others;

          // Mise à jour du state global
          if (mainAddress) {
            this.appfacade.setDefaultAddressState(mainAddress);
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
  deleteAddress(userId: number, addressId: number): void {
    this.appfacade.deleteAddress(userId, addressId).subscribe({
      next: () => {
        this.successMessage = 'Adresse supprimée avec succès.';

        // Masquer le message après 3 secondes
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);

        this.loadUserAndAddress(); // Recharger les adresses après suppression
      },
      error: (error) => {
        console.error("Erreur lors de la suppression de l'adresse :", error);
        this.errorMessage =
          "Impossible de supprimer l'adresse. Veuillez réessayer.";
      },
    });
  }
}
