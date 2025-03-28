import { Component, DestroyRef, inject } from '@angular/core';
import { CommandResponse } from '../../models/commandResponse.model';
import { OrderService } from 'src/app/services/order.service';
import { CommandStatus } from '../../models/commandStatus.enum';
import { catchError, of, tap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-order-list',
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.css'],
})
export class OrderListComponent {
  orders: CommandResponse[] = [];
  statuses = Object.values(CommandStatus); // On récupère tous les statuts de l'énum
  private destroyRef = inject(DestroyRef);

  // Mappage des statuts anglais vers français
  statusMap: { [key: string]: string } = {
    PENDING: 'EN ATTENTE',
    PAID: 'PAYÉ',
    PROCESSING: 'EN TRAITEMENT',
    SHIPPED: 'EXPÉDIÉ',
    DELIVERED: 'LIVRÉ',
    CANCELLED: 'ANNULÉ',
  };

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.orderService
      .getAllOrders()
      .pipe(
        tap((orders) => {
          if (Array.isArray(orders)) {
            this.orders = orders.map((order) => ({
              ...order,
              // Convertir les statuts en français lors de la récupération
              status: this.statusMap[order.status] || order.status,
            }));
          } else {
            console.error('Données invalides reçues :', orders);
            this.orders = []; // Vider le tableau en cas de données invalides
          }
        }),
        catchError((error) => {
          console.error(
            'Erreur lors de la récupération des utilisateurs:',
            error
          );
          this.orders = [];
          return of([]); // Retourner un tableau vide en cas d'erreur
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe();
  }

  onStatusChange(order: CommandResponse): void {
    // Convertir le statut en français vers l'anglais avant d'envoyer au backend
    const backendStatus =
      Object.keys(this.statusMap).find(
        (key) => this.statusMap[key] === order.status
      ) || order.status;

    this.orderService
      .updateOrderStatus(order.id, backendStatus)
      .pipe(
        catchError((error) => {
          console.error('Erreur lors de la mise à jour du statut:', error);
          return of(null); // Retourne un observable avec une valeur nulle en cas d'erreur
        })
      )
      .subscribe(() => {
        console.log('Statut mis à jour avec succès');
      });
  }

  deleteOrder() {
    // Implémentez ici la logique pour supprimer une commande si nécessaire
  }
}
