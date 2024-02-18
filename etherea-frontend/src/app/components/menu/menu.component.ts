import { Component } from '@angular/core';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css'],
})
export class MenuComponent {
  isMenuOpen: boolean = false;
  categories: string[] = [
    'Crème de jour',
    'Crème de nuit',
    'Crème pour les yeux',
    'Crème solaire',
    'Nouveauté',
  ];

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  closeMenu() {
    this.isMenuOpen = false;
  }
}
