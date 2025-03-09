import { Component, OnInit } from '@angular/core';
import { CookieConsentService } from 'src/app/services/cookie-consent.service';
import { SaveCookieConsentRequest } from '../models/SaveCookieConsentRequest.model';
import { CookieChoice } from '../models/cookie-choice.model';

@Component({
  selector: 'app-cookie-popup',
  templateUrl: './cookie-popup.component.html',
  styleUrls: ['./cookie-popup.component.css'],
})
export class CookiePopupComponent implements OnInit {
  showPopup: boolean = false;
  essentialCookies: string[] = [];
  nonEssentialCookies: string[] = [];

  constructor(private cookieConsentService: CookieConsentService) {}

  ngOnInit() {
    const consentGiven = localStorage.getItem('cookie_consent');
    if (!consentGiven) {
      this.showPopup = true;
      this.loadCookieConfig();
    }
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

  acceptAllCookies() {
    // Transformer les noms de cookies en objets CookieChoice avec la propriété accepted
    const allCookies: CookieChoice[] = [
      ...this.essentialCookies,
      ...this.nonEssentialCookies,
    ].map((cookieName) => ({
      cookieName,
      accepted: true, // On accepte tous les cookies
    }));

    const request: SaveCookieConsentRequest = {
      userId: 1,
      cookiePolicyVersion: '1.0',
      cookieChoices: allCookies,
    };

    console.log(
      'Envoi de la requête pour accepter tous les cookies...',
      request
    );

    this.cookieConsentService.acceptAllCookies(request).subscribe({
      next: (response) => {
        console.log('Cookies acceptés avec succès :', response);
        localStorage.setItem('cookie_consent', 'accepted');
        this.showPopup = false;
      },
      error: (error) => {
        console.error('Erreur lors de l’acceptation des cookies :', error);
      },
    });
  }
}
