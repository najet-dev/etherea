import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-menu',
  templateUrl: './admin-menu.component.html',
  styleUrls: ['./admin-menu.component.css'],
})
export class AdminMenuComponent {
  isMenuActive = false;
  constructor(private router: Router) {}

  toggleMenu() {
    this.isMenuActive = !this.isMenuActive;
  }
  goToSite() {
    this.router.navigate(['/']); // ou n'importe quelle route publique
  }
}
