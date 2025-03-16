import { Component, ViewChild } from '@angular/core';
import { CookiePopupComponent } from '../cookie-popup/cookie-popup.component';

@Component({
  selector: 'app-legal-information',
  templateUrl: './legal-information.component.html',
  styleUrls: ['./legal-information.component.css'],
})
export class LegalInformationComponent {
  @ViewChild(CookiePopupComponent) cookiePopup!: CookiePopupComponent;

  showCookiePopup(): void {
    if (this.cookiePopup) {
      this.cookiePopup.showCustomization = false; // Fermer d'abord
      this.cookiePopup.ngOnInit(); // Rafraîchir la liste des cookies
      setTimeout(() => {
        this.cookiePopup.openPopup(); // Réouvrir après mise à jour
      }, 100); // Laisser un léger délai pour éviter les conflits d'affichage
    }
  }
}
