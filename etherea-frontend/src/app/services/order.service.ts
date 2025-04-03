import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable, tap, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CommandResponse } from '../components/models/commandResponse.model';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  apiUrl = environment.apiUrl;

  constructor(private httpClient: HttpClient) {}

  // Récupérer toutes les commandes d'un utilisateur
  getUserCommands(userId: number): Observable<CommandResponse[]> {
    return this.httpClient.get<CommandResponse[]>(
      `${this.apiUrl}/command/user/${userId}`
    );
  }

  // Récupérer une seule commande d'un utilisateur
  getUserCommandById(
    userId: number,
    commandId: number
  ): Observable<CommandResponse> {
    return this.httpClient.get<CommandResponse>(
      `${this.apiUrl}/command/user/${userId}/command/${commandId}`
    );
  }
}
