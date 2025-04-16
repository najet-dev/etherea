import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Newsletter } from '../components/models/newsletter.model';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { NewsletterSend } from '../components/models/newsletterSend.model';

@Injectable({
  providedIn: 'root',
})
export class NewsletterService {
  apiUrl = environment.apiUrl;

  constructor(private httpClient: HttpClient) {}

  subscribeToNewsletter(
    newsletter: Newsletter
  ): Observable<{ message: string }> {
    return this.httpClient
      .post<{ message: string }>(
        `${this.apiUrl}/newsletter/subscribe`,
        newsletter
      )
      .pipe(
        tap((response) => console.log('Réponse du serveur:', response.message)),
        catchError((error) => {
          console.error(
            "Erreur lors de l'inscription à la newsletter :",
            error
          );
          return throwError(
            () => new Error(error.error?.message || 'Erreur inconnue.')
          );
        })
      );
  }
  /**
   * Envoie la newsletter à tous les abonnés (réservé à l'admin).
   *
   * @param dto Objet contenant le sujet et le contenu HTML de la newsletter.
   * @returns Observable avec le message de succès ou d'erreur.
   */
  sendNewsletter(
    newsletterSend: NewsletterSend
  ): Observable<{ message: string }> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });

    return this.httpClient
      .post<{ message: string }>(
        `${this.apiUrl}/newsletter/send`,
        newsletterSend,
        {
          headers,
        }
      )
      .pipe(
        tap((response) =>
          console.log('Newsletter envoyée avec succès :', response.message)
        ),
        catchError((error) => {
          console.error('Erreur lors de l’envoi de la newsletter :', error);
          return throwError(
            () =>
              new Error(
                error.error?.message || 'Erreur inconnue lors de l’envoi.'
              )
          );
        })
      );
  }
}
