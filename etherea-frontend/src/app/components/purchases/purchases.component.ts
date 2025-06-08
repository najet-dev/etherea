import { Component, OnInit } from '@angular/core';
import { SignupRequest } from '../models/signupRequest.model';
import { AppFacade } from 'src/app/services/appFacade.service';
import { CommandResponse } from '../models/commandResponse.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-purchases',
  templateUrl: './purchases.component.html',
  styleUrls: ['./purchases.component.css'],
})
export class PurchasesComponent implements OnInit {
  user: SignupRequest | null = null;
  commands: CommandResponse[] = [];
  loading = true;
  errorMessage = '';

  // Map to translate order status from English to French
  statusMap: { [key: string]: string } = {
    PENDING: 'EN ATTENTE',
    PAID: 'PAYÉ',
    PROCESSING: 'EN TRAITEMENT',
    SHIPPED: 'EXPÉDIÉ',
    DELIVERED: 'LIVRÉ',
    CANCELLED: 'ANNULÉ',
  };

  constructor(private appFacade: AppFacade, private router: Router) {}

  ngOnInit(): void {
    // Fetch the currently logged-in user details
    this.appFacade.getCurrentUserDetails().subscribe({
      next: (data) => {
        this.user = data;
        if (this.user && this.user.id) {
          // If user has a valid ID, fetch their orders
          this.fetchUserCommands(this.user.id);
        } else {
          this.loading = false;
          this.errorMessage = 'Utilisateur non trouvé.';
        }
      },
      error: () => {
        this.loading = false;
        this.errorMessage =
          'Erreur lors de la récupération des informations utilisateur.';
      },
    });
  }

  fetchUserCommands(userId: number): void {
    // Fetch the list of orders for a specific user
    this.appFacade.getUserOrders(userId).subscribe({
      next: (data) => {
        this.commands = data.map((command) => ({
          ...command,
          // Translate the order status to French using the status map
          status: this.statusMap[command.status] || command.status,
        }));
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors de la récupération des commandes';
        this.loading = false;
      },
    });
  }

  // Navigate to the order details page
  viewCommandDetails(commandId: number): void {
    this.router.navigate([`/order-details/${commandId}`]);
  }
}
