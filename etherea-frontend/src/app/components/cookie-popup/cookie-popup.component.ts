import { Component, OnInit } from '@angular/core';
import { CookieConsentService } from 'src/app/services/cookie-consent.service';
import { SaveCookieConsentRequest } from '../models/SaveCookieConsentRequest.model';
import { CookieChoice } from '../models/cookie-choice.model';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-cookie-popup',
  templateUrl: './cookie-popup.component.html',
  styleUrls: ['./cookie-popup.component.css'],
})
export class CookiePopupComponent implements OnInit {
  showBanner = false;
  showCustomization = false;
  cookieChoices: CookieChoice[] = [];
  essentialCookies: string[] = [];
  nonEssentialCookies: string[] = [];

  constructor(
    private cookieService: CookieService,
    private cookieConsentService: CookieConsentService
  ) {}

  ngOnInit(): void {
    const consentGiven = this.cookieService.get('cookieConsent');
    this.showBanner = !consentGiven; // Afficher ou non le pop-up en fonction du consentement
    this.loadCookieConfig();
  }

  /**
   * Charger la configuration des cookies (essentiels et non-essentiels)
   */
  loadCookieConfig() {
    this.cookieConsentService.getCookiesConfig().subscribe({
      next: (response) => {
        this.essentialCookies = response.essential;
        this.nonEssentialCookies = response['non-essential'];
        console.log('Cookies config loaded:', response);
      },
      error: (error) => {
        console.error(
          'Erreur lors du chargement de la configuration des cookies',
          error
        );
      },
    });
  }

  /**
   * Accepter tous les cookies (essentiels et non-essentiels)
   */
  acceptAll(): void {
    const allCookies = [
      ...this.essentialCookies.map((name: string) => ({
        cookieName: name,
        accepted: true,
      })),
      ...this.nonEssentialCookies.map((name: string) => ({
        cookieName: name,
        accepted: true,
      })),
    ];

    this.cookieConsentService.getSessionId().subscribe({
      next: (sessionId) => {
        const request: SaveCookieConsentRequest = {
          userId: null,
          sessionId: sessionId,
          cookiePolicyVersion: '1.0',
          cookieChoices: allCookies,
        };

        console.log('Request to accept all cookies:', request);

        this.cookieConsentService.acceptAllCookies(request).subscribe({
          next: (response) => {
            console.log('Consent saved:', response);
            this.cookieService.set('cookieConsent', 'accepted', 30, '/');
            this.showBanner = false;
          },
          error: (error) => {
            console.error('Erreur lors de l’acceptation des cookies:', error);
          },
        });
      },
      error: (error) => {
        console.error('Erreur lors de la récupération du sessionId:', error);
      },
    });
  }

  /**
   * Rejeter tous les cookies (y compris les non-essentiels)
   */
  rejectAll(): void {
    const request: SaveCookieConsentRequest = {
      userId: null, // Ou récupérez un ID d'utilisateur si nécessaire
      sessionId: this.cookieService.get('sessionId') || null,
      cookiePolicyVersion: '1.0',
      cookieChoices: this.essentialCookies.map((cookieName) => ({
        cookieName,
        accepted: false,
      })),
    };

    this.cookieConsentService.rejectAllCookies(request).subscribe(() => {
      this.cookieService.set('cookieConsent', 'rejected', 30, '/'); // Sauvegarder le rejet dans un cookie
      this.showBanner = false;
    });
  }

  /**
   * Ouvrir le panneau de personnalisation des cookies
   */
  openCustomization(): void {
    this.showCustomization = true;
  }

  /**
   * Sauvegarder les choix de cookies personnalisés
   */
  saveCustomChoices(): void {
    const request: SaveCookieConsentRequest = {
      userId: null, // Ou récupérez un ID d'utilisateur si nécessaire
      sessionId: this.cookieService.get('sessionId') || null,
      cookiePolicyVersion: '1.0',
      cookieChoices: this.cookieChoices,
    };

    this.cookieConsentService.customizeCookies(request).subscribe(() => {
      this.cookieService.set('cookieConsent', 'custom', 30, '/'); // Sauvegarder le consentement personnalisé dans un cookie
      this.showCustomization = false;
      this.showBanner = false;
    });
  }

  /**
   * Fermer la personnalisation sans enregistrer
   */
  closeCustomization(): void {
    this.showCustomization = false;
  }
}
