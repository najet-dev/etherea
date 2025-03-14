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
      this.cookiePopup.openPopup();
    }
  }
}
