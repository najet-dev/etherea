import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  BehaviorSubject,
  catchError,
  Observable,
  of,
  tap,
  throwError,
} from 'rxjs';
import { environment } from 'src/environments/environment';
import { CommandStatus } from '../components/models/commandStatus.enum';
import { CommandResponse } from '../components/models/commandResponse.model';
import { CommandItem } from '../components/models/CommandItem.model';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  apiUrl = environment.apiUrl;

  constructor(private httpClient: HttpClient) {}

  // Récupérer toutes les commandes d'un utilisateur
  getAllOrders(): Observable<CommandResponse[]> {
    return this.httpClient.get<CommandResponse[]>(`${this.apiUrl}/command`);
  }

  // Récupérer une seule commande d'un utilisateur
  getUserOrders(userId: number): Observable<CommandResponse[]> {
    return this.httpClient.get<CommandResponse[]>(
      `${this.apiUrl}/command/user/${userId}`
    );
  }

  // Récupérer une commande d'un utilisateur spécifique
  getUserOrderById(
    userId: number,
    commandId: number
  ): Observable<CommandResponse> {
    return this.httpClient.get<CommandResponse>(
      `${this.apiUrl}/command/user/${userId}/command/${commandId}`
    );
  }
  getCommandById(id: number): Observable<CommandItem[]> {
    return this.httpClient
      .get<CommandItem[]>(`${this.apiUrl}/command/${id}/items`) // Notez le changement de type
      .pipe(
        tap((products) => {
          console.log('Fetched product from API:', products);
        }),
        catchError((error) => {
          console.error('Error fetching product from API:', error);
          return of([]); // Retourne un tableau vide en cas d'erreur
        })
      );
  }

  // Mapper les statuts en français vers l'anglais pour le backend
  private statusMap: { [key: string]: string } = {
    'EN ATTENTE': 'PENDING',
    PAYÉ: 'PAID',
    'EN TRAITEMENT': 'PROCESSING',
    EXPÉDIÉ: 'SHIPPED',
    LIVRÉ: 'DELIVERED',
    ANNULÉ: 'CANCELLED',
  };

  // Vérifier si le statut existe dans le mapping et le retourner
  private mapToBackendStatus(newStatus: string): string {
    return this.statusMap[newStatus] || newStatus;
  }

  // Mise à jour du statut d'une commande
  updateOrderStatus(
    orderId: number,
    newStatus: string
  ): Observable<CommandStatus> {
    const backendStatus = this.mapToBackendStatus(newStatus);

    // Vérification si le statut est valide (optionnel)
    if (!backendStatus) {
      // Utilisation de throwError avec une fonction génératrice d'erreur
      return throwError(() => new Error('Statut invalide.'));
    }

    // Appel API pour mettre à jour le statut de la commande
    return this.httpClient
      .put<CommandStatus>(
        `${this.apiUrl}/command/${orderId}/status?newStatus=${backendStatus}`,
        {}
      )
      .pipe(
        catchError((error) => {
          console.error('Erreur lors de la mise à jour du statut:', error);
          return of(error); // On retourne l'erreur si elle survient
        })
      );
  }
}
