import { Component, OnInit } from '@angular/core';
import { CommandItem } from '../models/CommandItem.model';
import { OrderService } from 'src/app/services/order.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-order-details',
  templateUrl: './order-details.component.html',
  styleUrls: ['./order-details.component.css'],
})
export class OrderDetailsComponent implements OnInit {
  commandItems: CommandItem[] = [];
  loading = true;
  errorMessage = '';

  constructor(
    private orderService: OrderService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const commandId = +this.route.snapshot.paramMap.get('id')!; // Récupérer l'ID de la commande depuis l'URL
    this.fetchOrderDetails(commandId);
  }

  fetchOrderDetails(commandId: number): void {
    this.orderService.getCommandById(commandId).subscribe({
      next: (data) => {
        console.log('Fetched products from API:', data);
        this.commandItems = data; // Assignez le tableau de produits
        this.loading = false; // Le chargement est terminé
      },
      error: (error) => {
        this.errorMessage =
          'Erreur lors de la récupération des détails de la commande';
        this.loading = false;
      },
    });
  }
}
