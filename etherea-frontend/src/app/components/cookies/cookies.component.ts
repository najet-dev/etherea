import { Component, ViewChild } from '@angular/core';
import { CookiePopupComponent } from '../cookie-popup/cookie-popup.component';

@Component({
  selector: 'app-cookies',
  templateUrl: './cookies.component.html',
  styleUrls: ['./cookies.component.css'],
})
export class CookiesComponent {
  @ViewChild(CookiePopupComponent) cookiePopup!: CookiePopupComponent;

  showCookiePopup(): void {
    if (this.cookiePopup) {
      this.cookiePopup.openPopup();
    }
  }
}
