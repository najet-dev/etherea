import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CookieConsentService } from 'src/app/services/cookie-consent.service';
import { CookieChoice } from '../models/cookie-choice.model';
import { StorageService } from 'src/app/services/storage.service';

@Component({
  selector: 'app-cookie-popup',
  templateUrl: './cookie-popup.component.html',
  styleUrls: ['./cookie-popup.component.css'],
})
export class CookiePopupComponent implements OnInit {
  showBanner = false;
  showCustomization = false;
  cookieChoices: CookieChoice[] = [];
  userId: number | null = null;
  sessionId: string | null = null;
  isLoading = true;

  constructor(
    private cookieConsentService: CookieConsentService,
    private storageService: StorageService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cookieConsentService.getSessionId().subscribe({
      next: (sessionId) => {
        if (sessionId) {
          this.sessionId = sessionId;
        } else {
          console.error('Session ID introuvable.');
        }
      },
      error: (error) => {
        console.error('Erreur lors de la récupération du Session ID.', error);
      },
    });

    // Récupérer la liste des cookies
    this.cookieConsentService.getCookiesList().subscribe({
      next: (cookies) => {
        console.log('Cookies récupérés :', cookies);

        // Seuls les cookies non essentiels sont personnalisables
        this.cookieChoices = cookies.filter((cookie) => !cookie.accepted);

        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Erreur lors du chargement des cookies', error);
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });

    const hasConsented = this.storageService.getItem('cookieConsent');
    console.log('Consentement déjà enregistré ?', hasConsented);
    this.showBanner = !hasConsented; // La bannière ne s'affichera plus si l'utilisateur a déjà consenti.
  }

  /**
   * Accepte tous les cookies et enregistre le consentement
   */
  acceptAllCookies(): void {
    if (!this.sessionId) {
      console.error('Erreur : sessionId non défini.');
      return;
    }

    this.cookieConsentService
      .acceptAllCookies(this.sessionId)
      .subscribe((response) => {
        if (response) {
          this.storageService.setItem('cookieConsent', 'true');
          this.showBanner = false;
        }
      });
  }

  /**
   * Refuse tous les cookies
   */
  rejectCookies(): void {
    if (!this.sessionId) {
      console.error('Erreur : sessionId non défini.');
      return;
    }

    this.cookieConsentService
      .rejectAllCookies(this.sessionId)
      .subscribe((response) => {
        if (response) {
          this.storageService.setItem('cookieConsent', 'true');
          this.showBanner = false;
        }
      });
  }

  /**
   * Affiche ou masque le modal de personnalisation des cookies
   */
  toggleCustomization(): void {
    this.showCustomization = !this.showCustomization;
    console.log('Valeur de showCustomization :', this.showCustomization);
    this.cdr.detectChanges();
  }

  /**
   * Enregistre les choix personnalisés des cookies
   */
  saveCustomChoices(): void {
    if (!this.sessionId) {
      console.error('Erreur : sessionId non défini.');
      return;
    }

    console.log(
      'Tentative d’enregistrement des choix cookies...',
      this.cookieChoices
    );

    this.cookieConsentService
      .customizeCookies(this.sessionId, [...this.cookieChoices])
      .subscribe({
        next: (response) => {
          console.log('Consentement personnalisé enregistré:', response);

          if (response) {
            // Stocke le consentement et masque la bannière
            this.storageService.setItem('cookieConsent', 'true');
            this.showCustomization = false;
            this.showBanner = false;

            console.log('Bannière masquée.');

            // Met à jour l'affichage
            this.cdr.markForCheck();
          }
        },
        error: (error) => {
          console.error(
            'Erreur lors de l’enregistrement du consentement.',
            error
          );
        },
      });
  }

  openPopup(): void {
    this.showCustomization = true;
    this.cdr.detectChanges();
  }
}
