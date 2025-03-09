import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { CookieConsent } from '../components/models/CookieConsent.model';
import { SaveCookieConsentRequest } from '../components/models/SaveCookieConsentRequest.model';

@Injectable({
  providedIn: 'root',
})
export class CookieConsentService {
  private readonly apiUrl = `${environment.apiUrl}/cookies`;

  constructor(private httpClient: HttpClient) {}

  /**
   * Récupérer le consentement d'un utilisateur
   * @param userId ID de l'utilisateur
   */
  getUserConsent(userId: number): Observable<CookieConsent> {
    return this.httpClient.get<CookieConsent>(`${this.apiUrl}/${userId}`).pipe(
      catchError((error) => {
        console.error('Erreur lors de la récupération du consentement:', error);
        return throwError(
          () => new Error('Impossible de récupérer le consentement.')
        );
      })
    );
  }
  /**
   * Récupérer la configuration des cookies (essentiels et non-essentiels)
   */
  getCookiesConfig(): Observable<any> {
    return this.httpClient.get<any>(`${this.apiUrl}/config`);
  }

  /**
   * Accepter tous les cookies (essentiels + non-essentiels)
   * @param request Objet contenant userId et policyVersion
   */
  acceptAllCookies(
    request: SaveCookieConsentRequest
  ): Observable<CookieConsent> {
    console.log('Request to accept all cookies:', request);
    return this.httpClient
      .post<CookieConsent>(`${this.apiUrl}/accept-all`, request, {
        withCredentials: true,
      })
      .pipe(
        catchError((error) => {
          console.error('Erreur lors de l’acceptation des cookies:', error);
          return throwError(
            () => new Error('Impossible d’accepter tous les cookies.')
          );
        })
      );
  }

  /**
   * Rejeter tous les cookies sauf les essentiels
   * @param request Objet contenant userId et policyVersion
   */
  rejectAllCookies(
    request: SaveCookieConsentRequest
  ): Observable<CookieConsent> {
    return this.httpClient
      .post<CookieConsent>(`${this.apiUrl}/reject-all`, request)
      .pipe(
        catchError((error) => {
          console.error('Erreur lors du rejet des cookies:', error);
          return throwError(
            () => new Error('Impossible de rejeter les cookies.')
          );
        })
      );
  }

  /**
   * Personnaliser les cookies
   * @param request Objet contenant userId, policyVersion et les choix de cookies
   */
  customizeCookies(
    request: SaveCookieConsentRequest
  ): Observable<CookieConsent> {
    return this.httpClient
      .post<CookieConsent>(`${this.apiUrl}/customize`, request)
      .pipe(
        catchError((error) => {
          console.error(
            'Erreur lors de la personnalisation des cookies:',
            error
          );
          return throwError(
            () => new Error('Impossible de personnaliser les cookies.')
          );
        })
      );
  }
}
