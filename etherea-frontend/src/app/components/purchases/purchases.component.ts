import { Component, OnInit } from '@angular/core';
import { OrderService } from 'src/app/services/order.service';
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

  // Mappage des statuts anglais vers français
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
    this.appFacade.getCurrentUserDetails().subscribe({
      next: (data) => {
        this.user = data;
        if (this.user && this.user.id) {
          // Vérifie que l'utilisateur a bien un ID
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
    this.appFacade.getUserOrders(userId).subscribe({
      next: (data) => {
        this.commands = data.map((command) => ({
          ...command,
          // Convertir les statuts anglais en français
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
  // Méthode pour afficher les détails de la commande
  viewCommandDetails(commandId: number): void {
    this.router.navigate([`/command-details/${commandId}`]);
  }
}
