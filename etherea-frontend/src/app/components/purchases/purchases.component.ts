import { Component, OnInit } from '@angular/core';
import { CommandResponse } from '../models/CommandResponse.model';
import { OrderService } from 'src/app/services/order.service';
import { SignupRequest } from '../models/SignupRequest.model';
import { AppFacade } from 'src/app/services/appFacade.service';

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

  constructor(
    private orderService: OrderService,
    private appFacade: AppFacade
  ) {}

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
    this.orderService.getUserCommands(userId).subscribe({
      next: (data) => {
        this.commands = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors de la récupération des commandes';
        this.loading = false;
      },
    });
  }
}
