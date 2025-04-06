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
export class AddressesComponent implements OnInit {
  deliveryAddresses: DeliveryAddress[] = []; // Modification : tableau d'adresses
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
          // Récupération de toutes les adresses utilisateur
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
          if (addresses.length > 0) {
            // Séparer l'adresse principale des autres
            const mainAddress = addresses.find(
              (address) => address.isDefault === true
            );
            const otherAddresses = addresses
              .filter((address) => address.isDefault !== true)
              .sort((a, b) => b.id - a.id); // Trier du plus récent au plus ancien

            // Mettre l'adresse principale en premier
            this.deliveryAddresses = mainAddress
              ? [mainAddress, ...otherAddresses]
              : otherAddresses;
          } else {
            this.deliveryAddresses = [];
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
}
