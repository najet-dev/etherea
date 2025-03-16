import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Contact } from '../components/models/contact.model';

@Injectable({
  providedIn: 'root',
})
export class ContactServiceService {
  private apiUrl = `${environment.apiUrl}/contacts`;

  constructor(private httpClient: HttpClient) {}

  sendMessage(contact: Contact): Observable<Contact> {
    return this.httpClient
      .post<Contact>(`${this.apiUrl}/save-message`, contact)
      .pipe(
        tap((response) => console.log('Message envoyé avec succès:', response)),
        catchError((error) => {
          console.error("Erreur lors de l'envoi du message:", error);
          return throwError(
            () =>
              new Error(
                "Erreur lors de l'envoi du message. Veuillez réessayer plus tard."
              )
          );
        })
      );
  }
}
