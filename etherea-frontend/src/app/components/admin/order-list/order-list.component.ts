import { Component, DestroyRef, inject } from '@angular/core';
import { CommandResponse } from '../../models/commandResponse.model';
import { CommandStatus } from '../../models/commandStatus.enum';
import { catchError, of, tap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AppFacade } from 'src/app/services/appFacade.service';

@Component({
  selector: 'app-order-list',
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.css'],
})
export class OrderListComponent {
  orders: CommandResponse[] = [];
  statuses = Object.values(CommandStatus); // Récupère tous les statuts
  private destroyRef = inject(DestroyRef);

  currentPage: number = 0;
  totalPages: number = 1;
  pageSize: number = 10; // Nombre de commandes par page

  // Mappage des statuts anglais vers français
  statusMap: { [key: string]: string } = {
    PENDING: 'EN ATTENTE',
    PAID: 'PAYÉ',
    PROCESSING: 'EN TRAITEMENT',
    SHIPPED: 'EXPÉDIÉ',
    DELIVERED: 'LIVRÉ',
    CANCELLED: 'ANNULÉ',
  };

  constructor(private appFacade: AppFacade) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.appFacade
      .getAllOrders(this.currentPage, this.pageSize)
      .pipe(
        tap((response) => {
          if (response && Array.isArray(response.content)) {
            this.orders = response.content.map((order) => ({
              ...order,
              status: this.statusMap[order.status] || order.status,
            }));
            this.totalPages = response.totalPages; // Mise à jour du nombre total de pages
          } else {
            console.error('Données invalides reçues :', response);
            this.orders = [];
          }
        }),
        catchError((error) => {
          console.error(
            'Erreur lors de la récupération des commandes :',
            error
          );
          this.orders = [];
          return of([]);
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe();
  }

  onStatusChange(order: CommandResponse): void {
    const backendStatus =
      Object.keys(this.statusMap).find(
        (key) => this.statusMap[key] === order.status
      ) || order.status;

    this.appFacade
      .updateOrderStatus(order.id, backendStatus)
      .pipe(
        catchError((error) => {
          console.error('Erreur lors de la mise à jour du statut:', error);
          return of(null);
        })
      )
      .subscribe(() => {
        console.log('Statut mis à jour avec succès');
      });
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadOrders();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadOrders();
    }
  }
}
