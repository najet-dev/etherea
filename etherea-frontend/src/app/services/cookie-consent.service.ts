import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { CookieService } from 'ngx-cookie-service';
import { CookieConsent } from '../components/models/cookieConsent.model';
import { SaveCookieConsentRequest } from '../components/models/saveCookieConsentRequest.model';
import { CookiePolicyVersion } from '../components/models/cookiePolicyVersion.enum';
import { CookieChoice } from '../components/models/cookie-choice.model';

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
   * Récupère le sessionId depuis le cookie ou le backend
   */
  getSessionId(): Observable<string> {
    // Vérification du cookie 'sessionId'
    const sessionId = this.cookieService.get('sessionId');
    console.log('sessionId récupéré depuis le cookie:', sessionId);

    // Si le sessionId existe déjà, on le retourne directement
    if (sessionId) {
      return of(sessionId);
    }

    return this.httpClient
      .get<{ sessionId: string }>(`${this.apiUrl}/session`, {
        withCredentials: true,
      })
      .pipe(
        switchMap((response) => {
          // Récupération du sessionId et stockage dans le cookie
          if (response && response.sessionId) {
            this.cookieService.set('sessionId', response.sessionId, 30, '/');
            return of(response.sessionId);
          }
          return of(''); // Cas où sessionId n'est pas reçu
        }),
        catchError((error) => {
          console.error('Erreur lors de la récupération du sessionId', error);
          return of('');
        })
      );
  }

  /**
   * Accepte tous les cookies et enregistre le consentement
   */
  acceptAllCookies(sessionId: string): Observable<CookieConsent | null> {
    if (!sessionId) {
      console.error(
        'Erreur : sessionId est requis pour enregistrer le consentement.'
      );
      return of(null);
    }

    const request: SaveCookieConsentRequest = {
      userId: null,
      sessionId,
      cookiePolicyVersion: CookiePolicyVersion.V1_0,
      cookieChoices: [],
    };

    return this.httpClient
      .post<CookieConsent>(`${this.apiUrl}/accept-all`, request, {
        withCredentials: true,
      })
      .pipe(
        catchError((error) => {
          console.error("Erreur lors de l'acceptation des cookies", error);
          return of(null);
        })
      );
  }
  /**
   * Rejette tous les cookies et enregistre le consentement
   */
  rejectAllCookies(sessionId: string): Observable<CookieConsent | null> {
    if (!sessionId) {
      console.error(
        'Erreur : sessionId est requis pour enregistrer le refus des cookies.'
      );
      return of(null);
    }

    const request: SaveCookieConsentRequest = {
      userId: null,
      sessionId,
      cookiePolicyVersion: CookiePolicyVersion.V1_0,
      cookieChoices: [],
    };

    return this.httpClient
      .post<CookieConsent>(`${this.apiUrl}/reject-all`, request, {
        withCredentials: true,
      })
      .pipe(
        catchError((error) => {
          console.error('Erreur lors du rejet des cookies', error);
          return of(null);
        })
      );
  }
  /**
   * Personnalise les cookies en fonction des choix de l'utilisateur et enregistre le consentement
   */
  customizeCookies(
    sessionId: string,
    cookieChoices: CookieChoice[]
  ): Observable<CookieConsent | null> {
    if (!sessionId) {
      console.error(
        'Erreur : sessionId est requis pour enregistrer la personnalisation des cookies.'
      );
      return of(null);
    }

    const request: SaveCookieConsentRequest = {
      userId: null,
      sessionId,
      cookiePolicyVersion: CookiePolicyVersion.V1_0,
      cookieChoices,
    };

    return this.httpClient
      .post<CookieConsent>(`${this.apiUrl}/customize`, request, {
        withCredentials: true,
      })
      .pipe(
        catchError((error) => {
          console.error(
            'Erreur lors de la personnalisation des cookies',
            error
          );
          return of(null);
        })
      );
  }
  getCookiesList(): Observable<CookieChoice[]> {
    return this.httpClient
      .get<{ essential: string[]; 'non-essential': string[] }>(
        `${this.apiUrl}/cookies-list`
      )
      .pipe(
        switchMap((cookies) => {
          const cookieChoices: CookieChoice[] = [
            ...cookies.essential.map((cookieName) => ({
              cookieName,
              accepted: true, // Cookies essentiels activés par défaut
            })),
            ...cookies['non-essential'].map((cookieName) => ({
              cookieName,
              accepted: false, // Cookies non essentiels désactivés par défaut
            })),
          ];
          return of(cookieChoices);
        }),
        catchError((error) => {
          console.error(
            'Erreur lors de la récupération de la liste des cookies',
            error
          );
          return of([]); // Retourne une liste vide en cas d'erreur
        })
      );
  }
}
