import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { CookieService } from 'ngx-cookie-service';
import { CookieConsent } from '../components/models/CookieConsent.model';
import { SaveCookieConsentRequest } from '../components/models/SaveCookieConsentRequest.model';

@Injectable({
  providedIn: 'root',
})
export class CookieConsentService {
  private readonly apiUrl = `${environment.apiUrl}/cookies`;

  constructor(
    private httpClient: HttpClient,
    private cookieService: CookieService
  ) {}

  /**
   * Centraliser la gestion du sessionId, soit en récupérant celui du cookie, soit en le générant.
   */
  private getSessionIdOrCreate(): Observable<string> {
    const sessionId = this.cookieService.get('sessionId');
    console.log('sessionId from cookie:', sessionId); // Debug
    if (sessionId) {
      return new Observable((observer) => observer.next(sessionId));
    }

    return this.httpClient
      .get<string>(`${this.apiUrl}/session`, { responseType: 'text' as 'json' })
      .pipe(
        switchMap((newSessionId: string) => {
          this.cookieService.set('sessionId', newSessionId, 30, '/');
          console.log('New sessionId set:', newSessionId); // Debug
          return new Observable<string>((observer) =>
            observer.next(newSessionId)
          );
        }),
        catchError((error) => {
          console.error('Erreur lors de la récupération du sessionId:', error);
          return throwError(
            () => new Error('Impossible de récupérer le sessionId.')
          );
        })
      );
  }

  /**
   * Récupérer ou générer un sessionId en appelant l'API backend.
   */
  public getSessionId(): Observable<string> {
    return this.getSessionIdOrCreate();
  }

  /**
   * Récupérer le consentement d'un utilisateur via userId ou sessionId.
   */
  getUserConsent(userId?: number): Observable<CookieConsent> {
    if (userId) {
      return this.httpClient
        .get<CookieConsent>(`${this.apiUrl}?userId=${userId}`)
        .pipe(
          catchError((error) => {
            console.error(
              'Erreur lors de la récupération du consentement:',
              error
            );
            return throwError(
              () => new Error('Impossible de récupérer le consentement.')
            );
          })
        );
    }

    return this.getSessionId().pipe(
      switchMap((sessionId) =>
        this.httpClient
          .get<CookieConsent>(`${this.apiUrl}?sessionId=${sessionId}`)
          .pipe(
            catchError((error) => {
              console.error(
                'Erreur lors de la récupération du consentement via sessionId:',
                error
              );
              return throwError(
                () => new Error('Impossible de récupérer le consentement.')
              );
            })
          )
      )
    );
  }

  /**
   * Récupérer la configuration des cookies (essentiels et non-essentiels).
   */
  getCookiesConfig(): Observable<any> {
    return this.httpClient.get<any>(`${this.apiUrl}/config`);
  }

  /**
   * Accepter tous les cookies.
   */
  acceptAllCookies(
    request: SaveCookieConsentRequest
  ): Observable<CookieConsent> {
    console.log('Request to accept all cookies:', request); // DEBUG
    return this.getSessionId().pipe(
      switchMap((sessionId) => {
        request.sessionId = sessionId;
        return this.httpClient
          .post<CookieConsent>(`${this.apiUrl}/accept-all`, request)
          .pipe(
            tap((response) => console.log('Consent saved:', response)), // DEBUG
            catchError((error) => {
              console.error('Erreur lors de l’acceptation des cookies:', error);
              return throwError(
                () => new Error('Impossible d’accepter tous les cookies.')
              );
            })
          );
      })
    );
  }

  /**
   * Rejeter tous les cookies sauf les essentiels.
   */
  rejectAllCookies(
    request: SaveCookieConsentRequest
  ): Observable<CookieConsent> {
    return this.getSessionId().pipe(
      switchMap((sessionId) => {
        request.sessionId = sessionId;
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
      })
    );
  }

  /**
   * Personnaliser les choix de cookies.
   */
  customizeCookies(
    request: SaveCookieConsentRequest
  ): Observable<CookieConsent> {
    return this.getSessionId().pipe(
      switchMap((sessionId) => {
        request.sessionId = sessionId;
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
      })
    );
  }
}
